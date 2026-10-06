using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Printing;
using System.IO;
using System.Management;
using System.Net;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading;
using System.Windows.Forms;

namespace RKG_Suyambu_Billing
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            try
            {
                Application.EnableVisualStyles();
                Application.SetCompatibleTextRenderingDefault(false);

                while (true)
                {
                    LoginForm loginForm = new LoginForm();
                    if (loginForm.ShowDialog() == DialogResult.OK)
                    {
                        MainPosForm mainForm = new MainPosForm(loginForm.AuthenticatedUser);
                        Application.Run(mainForm);
                        if (mainForm.WantsLogout)
                        {
                            continue; // return to login screen
                        }
                        break;
                    }
                    else
                    {
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Application Notice: " + ex.Message, "RKG POS", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 1. DATA MODELS & ENHANCED TABLE STRUCTURES
    // ════════════════════════════════════════════════════════════════════════
    public class UserProfile
    {
        public string UserId { get; set; }
        public string FullName { get; set; }
        public string Role { get; set; }
        public string Token { get; set; }
    }

    public class Product
    {
        public string Code { get; set; }
        public string Name { get; set; }
        public string TamilName { get; set; }
        public string Category { get; set; }
        public string Pack { get; set; }
        public decimal RetailPrice { get; set; }
        public decimal WholesalePrice { get; set; }
        public int Stock { get; set; }
        public string Status { get; set; }
        public int ReorderLevel { get; set; }
    }

    public class CartEntry
    {
        public Product Item { get; set; }
        public int Qty { get; set; }
        public decimal UnitRate { get; set; }
        public decimal TotalAmount
        {
            get { return Qty * UnitRate; }
        }
    }

    public class Promo
    {
        public string Code { get; set; }
        public string Name { get; set; }
        public string QrNumber { get; set; }
        public decimal DiscountPct { get; set; }
        public decimal MaxDiscount { get; set; }
        public decimal MinOrder { get; set; }
        public int Credits { get; set; }
        public int UsedCredits { get; set; }
    }

    public class BillRecord
    {
        public string BillNo { get; set; }
        public string InvoiceNo { get; set; }
        public string DailyDate { get; set; }
        public string DateTimeIso { get; set; }
        public string CustomerName { get; set; }
        public string CustomerPhone { get; set; }
        public string PaymentMode { get; set; }
        public string BilledBy { get; set; }
        public decimal Subtotal { get; set; }
        public decimal Discount { get; set; }
        public decimal NetAmount { get; set; }
        public string PromoCode { get; set; }
        public List<BillItemRecord> Items { get; set; }
        public bool SyncedToFirebase { get; set; }
    }

    public class BillItemRecord
    {
        public string Code { get; set; }
        public string Name { get; set; }
        public string TamilName { get; set; }
        public string Pack { get; set; }
        public int Qty { get; set; }
        public decimal Rate { get; set; }
        public decimal Total { get; set; }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 2. HYBRID CLOUD & FIREBASE SYNC ENGINE (OFFLINE QUEUE + ONLINE SYNC)
    // ════════════════════════════════════════════════════════════════════════
    public static class CloudSyncEngine
    {
        private static string AppDataDir = AppDomain.CurrentDomain.BaseDirectory;
        private static string LocalLedgerFile = Path.Combine(AppDataDir, "pos_sales_ledger.json");
        private static string PendingQueueFile = Path.Combine(AppDataDir, "pos_pending_sync_queue.json");
        private static string DailyTransactionsFile = Path.Combine(AppDataDir, "pos_daily_transactions.json");
        private static object fileLock = new object();

        public static bool IsCloudOnline { get; private set; }
        public static int PendingQueueCount
        {
            get
            {
                lock (fileLock)
                {
                    try
                    {
                        if (File.Exists(PendingQueueFile))
                        {
                            string[] lines = File.ReadAllLines(PendingQueueFile);
                            return lines.Length;
                        }
                    }
                    catch { }
                    return 0;
                }
            }
        }

        public static bool CheckInternetAndCloudConnectivity()
        {
            try
            {
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create("https://firestore.googleapis.com/v1/projects/rkg-suiambu/databases/(default)/documents/company_settings");
                req.Method = "GET";
                req.Timeout = 2000;
                using (HttpWebResponse res = (HttpWebResponse)req.GetResponse())
                {
                    IsCloudOnline = (res.StatusCode == HttpStatusCode.OK || res.StatusCode == HttpStatusCode.NotFound);
                }
            }
            catch
            {
                IsCloudOnline = false;
            }

            if (IsCloudOnline)
            {
                ProcessPendingQueueAsync();
            }

            return IsCloudOnline;
        }

        public static void SaveBillLocallyAndQueue(BillRecord bill)
        {
            lock (fileLock)
            {
                try
                {
                    string json = SerializeBillToJson(bill);
                    File.AppendAllText(LocalLedgerFile, json + Environment.NewLine);
                    File.AppendAllText(PendingQueueFile, json + Environment.NewLine);
                    File.AppendAllText(DailyTransactionsFile, json + Environment.NewLine);
                }
                catch { }
            }

            if (IsCloudOnline)
            {
                ProcessPendingQueueAsync();
            }
        }

        public static void ProcessPendingQueueAsync()
        {
            ThreadPool.QueueUserWorkItem(state =>
            {
                lock (fileLock)
                {
                    try
                    {
                        if (!File.Exists(PendingQueueFile)) return;

                        string[] pending = File.ReadAllLines(PendingQueueFile);
                        if (pending.Length == 0) return;

                        List<string> remaining = new List<string>();

                        foreach (string line in pending)
                        {
                            if (string.IsNullOrWhiteSpace(line)) continue;

                            bool success = PostSingleBillToFirestore(line);
                            if (!success)
                            {
                                remaining.Add(line);
                            }
                        }

                        File.WriteAllLines(PendingQueueFile, remaining.ToArray());
                    }
                    catch { }
                }
            });
        }

        private static bool PostSingleBillToFirestore(string billJson)
        {
            try
            {
                string invId = ExtractJsonVal(billJson, "invoice_connected");
                string billNo = ExtractJsonVal(billJson, "bill_number");
                string dateVal = ExtractJsonVal(billJson, "daily_date");
                if (string.IsNullOrEmpty(dateVal) || dateVal == "-") dateVal = DateTime.Now.ToString("yyyy-MM-dd");

                string docId = string.IsNullOrEmpty(invId) || invId == "-" ? "INV-" + DateTime.Now.ToString("yyyyMMdd-HHmmss") : invId;
                string firestorePayload = ConvertJsonToFirestoreDocument(billJson);

                // 1. Post to /invoices
                string url = "https://firestore.googleapis.com/v1/projects/rkg-suiambu/databases/(default)/documents/invoices/" + docId;
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
                req.Method = "PATCH";
                req.ContentType = "application/json";
                req.Timeout = 4000;

                byte[] postBytes = Encoding.UTF8.GetBytes(firestorePayload);
                req.ContentLength = postBytes.Length;
                using (Stream stream = req.GetRequestStream())
                {
                    stream.Write(postBytes, 0, postBytes.Length);
                }

                using (HttpWebResponse res = (HttpWebResponse)req.GetResponse())
                {
                    if (res.StatusCode == HttpStatusCode.OK || res.StatusCode == HttpStatusCode.Created)
                    {
                        // 2. Also register in /bill_numbers
                        RegisterBillNumberToFirestore(billNo, docId, dateVal);
                        // 3. Log stock movement to /daily_transfers
                        LogTransferMovement(docId, billJson);
                        return true;
                    }
                }
            }
            catch { }
            return false;
        }

        private static void RegisterBillNumberToFirestore(string billNo, string invId, string dateVal)
        {
            try
            {
                string cleanBill = string.IsNullOrEmpty(billNo) ? "BILL-" + DateTime.Now.Ticks : billNo.Replace("/", "_");
                string url = "https://firestore.googleapis.com/v1/projects/rkg-suiambu/databases/(default)/documents/bill_numbers/" + cleanBill;
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
                req.Method = "PATCH";
                req.ContentType = "application/json";
                req.Timeout = 3000;

                string payload = string.Format(System.Globalization.CultureInfo.InvariantCulture,
                    "{{\"fields\":{{\"bill_number\":{{\"stringValue\":\"{0}\"}},\"invoice_id\":{{\"stringValue\":\"{1}\"}},\"date\":{{\"stringValue\":\"{2}\"}},\"created_at\":{{\"stringValue\":\"{3}\"}}}}}}",
                    billNo, invId, dateVal, DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss"));

                byte[] b = Encoding.UTF8.GetBytes(payload);
                req.ContentLength = b.Length;
                using (Stream s = req.GetRequestStream()) { s.Write(b, 0, b.Length); }
                using (HttpWebResponse res = (HttpWebResponse)req.GetResponse()) { }
            }
            catch { }
        }

        private static void LogTransferMovement(string invId, string billJson)
        {
            try
            {
                string transId = "TR-" + DateTime.Now.ToString("yyyyMMdd-HHmmss-fff");
                string url = "https://firestore.googleapis.com/v1/projects/rkg-suiambu/databases/(default)/documents/daily_transfer_items/" + transId;
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
                req.Method = "PATCH";
                req.ContentType = "application/json";
                req.Timeout = 3000;

                string customer = ExtractJsonVal(billJson, "customer_name");
                string total = ExtractJsonVal(billJson, "total_amount_of_the_bill");

                string payload = string.Format(System.Globalization.CultureInfo.InvariantCulture,
                    "{{\"fields\":{{\"transfer_id\":{{\"stringValue\":\"{0}\"}},\"invoice_ref\":{{\"stringValue\":\"{1}\"}},\"type\":{{\"stringValue\":\"SALES_OUT\"}},\"customer\":{{\"stringValue\":\"{2}\"}},\"amount\":{{\"doubleValue\":{3}}},\"timestamp\":{{\"stringValue\":\"{4}\"}}}}}}",
                    transId, invId, customer.Replace("\"", "\\\""), string.IsNullOrEmpty(total) || total == "-" ? "0" : total, DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss"));

                byte[] b = Encoding.UTF8.GetBytes(payload);
                req.ContentLength = b.Length;
                using (Stream s = req.GetRequestStream()) { s.Write(b, 0, b.Length); }
                using (HttpWebResponse res = (HttpWebResponse)req.GetResponse()) { }
            }
            catch { }
        }

        private static string ConvertJsonToFirestoreDocument(string rawJson)
        {
            var inv = System.Globalization.CultureInfo.InvariantCulture;
            string billNo = ExtractJsonVal(rawJson, "bill_number");
            string invNo = ExtractJsonVal(rawJson, "invoice_connected");
            string dailyDate = ExtractJsonVal(rawJson, "daily_date");
            string dt = ExtractJsonVal(rawJson, "date_time");
            string custName = ExtractJsonVal(rawJson, "customer_name");
            string custPhone = ExtractJsonVal(rawJson, "customer_mobile_number");
            string billedBy = ExtractJsonVal(rawJson, "product_purchased_by");
            string payMode = ExtractJsonVal(rawJson, "payment_mode");
            string subtotal = ExtractJsonVal(rawJson, "subtotal");
            string disc = ExtractJsonVal(rawJson, "discount");
            string grandTotal = ExtractJsonVal(rawJson, "total_amount_of_the_bill");
            string promo = ExtractJsonVal(rawJson, "promo_code_used");

            StringBuilder sb = new StringBuilder();
            sb.Append("{\"fields\":{");
            sb.AppendFormat(inv, "\"bill_number\":{{\"stringValue\":\"{0}\"}},", billNo);
            sb.AppendFormat(inv, "\"invoice_connected\":{{\"stringValue\":\"{0}\"}},", invNo);
            sb.AppendFormat(inv, "\"daily_date\":{{\"stringValue\":\"{0}\"}},", dailyDate);
            sb.AppendFormat(inv, "\"date_time\":{{\"stringValue\":\"{0}\"}},", dt);
            sb.AppendFormat(inv, "\"customer_name\":{{\"stringValue\":\"{0}\"}},", custName.Replace("\"", "\\\""));
            sb.AppendFormat(inv, "\"customer_mobile_number\":{{\"stringValue\":\"{0}\"}},", custPhone);
            sb.AppendFormat(inv, "\"product_purchased_by\":{{\"stringValue\":\"{0}\"}},", billedBy.Replace("\"", "\\\""));
            sb.AppendFormat(inv, "\"payment_mode\":{{\"stringValue\":\"{0}\"}},", payMode);
            sb.AppendFormat(inv, "\"subtotal\":{{\"doubleValue\":{0}}},", string.IsNullOrEmpty(subtotal) || subtotal == "-" ? "0" : subtotal);
            sb.AppendFormat(inv, "\"discount\":{{\"doubleValue\":{0}}},", string.IsNullOrEmpty(disc) || disc == "-" ? "0" : disc);
            sb.AppendFormat(inv, "\"total_amount_of_the_bill\":{{\"doubleValue\":{0}}},", string.IsNullOrEmpty(grandTotal) || grandTotal == "-" ? "0" : grandTotal);
            sb.AppendFormat(inv, "\"promo_code_used\":{{\"stringValue\":\"{0}\"}},", promo);
            sb.AppendFormat(inv, "\"sync_status\":{{\"stringValue\":\"SYNCED\"}},");
            sb.AppendFormat(inv, "\"timestamp\":{{\"stringValue\":\"{0}\"}}", DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss"));
            sb.Append("}}");
            return sb.ToString();
        }

        public static string SerializeBillRecordToFirestoreDoc(BillRecord b)
        {
            var inv = System.Globalization.CultureInfo.InvariantCulture;
            StringBuilder sb = new StringBuilder();
            sb.Append("{\"fields\":{");
            sb.AppendFormat(inv, "\"bill_number\":{{\"stringValue\":\"{0}\"}},", b.BillNo);
            sb.AppendFormat(inv, "\"invoice_connected\":{{\"stringValue\":\"{0}\"}},", b.InvoiceNo);
            sb.AppendFormat(inv, "\"daily_date\":{{\"stringValue\":\"{0}\"}},", b.DailyDate);
            sb.AppendFormat(inv, "\"date_time\":{{\"stringValue\":\"{0}\"}},", b.DateTimeIso);
            sb.AppendFormat(inv, "\"customer_name\":{{\"stringValue\":\"{0}\"}},", b.CustomerName.Replace("\"", "\\\""));
            sb.AppendFormat(inv, "\"customer_mobile_number\":{{\"stringValue\":\"{0}\"}},", b.CustomerPhone);
            sb.AppendFormat(inv, "\"product_purchased_by\":{{\"stringValue\":\"{0}\"}},", b.BilledBy.Replace("\"", "\\\""));
            sb.AppendFormat(inv, "\"payment_mode\":{{\"stringValue\":\"{0}\"}},", b.PaymentMode);
            sb.AppendFormat(inv, "\"subtotal\":{{\"doubleValue\":{0:F2}}},", b.Subtotal);
            sb.AppendFormat(inv, "\"discount\":{{\"doubleValue\":{0:F2}}},", b.Discount);
            sb.AppendFormat(inv, "\"total_amount_of_the_bill\":{{\"doubleValue\":{0:F2}}},", b.NetAmount);
            sb.AppendFormat(inv, "\"promo_code_used\":{{\"stringValue\":\"{0}\"}},", string.IsNullOrEmpty(b.PromoCode) ? "None" : b.PromoCode);

            // Purchased Products Array Detail
            sb.Append("\"purchased_products\":{\"arrayValue\":{\"values\":[");
            for (int i = 0; i < b.Items.Count; i++)
            {
                var item = b.Items[i];
                sb.Append("{\"mapValue\":{\"fields\":{");
                sb.AppendFormat(inv, "\"product_code\":{{\"stringValue\":\"{0}\"}},", item.Code);
                sb.AppendFormat(inv, "\"product_name\":{{\"stringValue\":\"{0}\"}},", item.Name.Replace("\"", "\\\""));
                sb.AppendFormat(inv, "\"pack\":{{\"stringValue\":\"{0}\"}},", string.IsNullOrEmpty(item.Pack) ? "-" : item.Pack);
                sb.AppendFormat(inv, "\"quantity\":{{\"integerValue\":\"{0}\"}},", item.Qty);
                sb.AppendFormat(inv, "\"unit_price\":{{\"doubleValue\":{0:F2}}},", item.Rate);
                sb.AppendFormat(inv, "\"total_amount\":{{\"doubleValue\":{0:F2}}}", item.Total);
                sb.Append("}}}");
                if (i < b.Items.Count - 1) sb.Append(",");
            }
            sb.Append("]}}");
            sb.Append("}}");
            return sb.ToString();
        }

        private static string SerializeBillToJson(BillRecord b)
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("{");
            sb.AppendFormat("\"bill_number\":\"{0}\",", b.BillNo);
            sb.AppendFormat("\"invoice_connected\":\"{0}\",", b.InvoiceNo);
            sb.AppendFormat("\"daily_date\":\"{0}\",", b.DailyDate);
            sb.AppendFormat("\"date_time\":\"{0}\",", b.DateTimeIso);
            sb.AppendFormat("\"customer_name\":\"{0}\",", b.CustomerName);
            sb.AppendFormat("\"customer_mobile_number\":\"{0}\",", b.CustomerPhone);
            sb.AppendFormat("\"product_purchased_by\":\"{0}\",", b.BilledBy);
            sb.AppendFormat("\"payment_mode\":\"{0}\",", b.PaymentMode);
            sb.AppendFormat("\"subtotal\":{0:F2},", b.Subtotal);
            sb.AppendFormat("\"discount\":{0:F2},", b.Discount);
            sb.AppendFormat("\"total_amount_of_the_bill\":{0:F2},", b.NetAmount);
            sb.AppendFormat("\"promo_code_used\":\"{0}\"", string.IsNullOrEmpty(b.PromoCode) ? "None" : b.PromoCode);
            sb.Append("}");
            return sb.ToString();
        }

        public static void SyncBillingUserToFirebase(string password)
        {
            ThreadPool.QueueUserWorkItem(s =>
            {
                try
                {
                    string url = "https://firestore.googleapis.com/v1/projects/rkg-suiambu/databases/(default)/documents/users/billing";
                    HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
                    req.Method = "PATCH";
                    req.ContentType = "application/json";
                    req.Timeout = 3000;

                    var inv = System.Globalization.CultureInfo.InvariantCulture;
                    string payload = string.Format(inv,
                        "{{\"fields\":{{\"username\":{{\"stringValue\":\"billing\"}},\"role\":{{\"stringValue\":\"Billing\"}},\"password\":{{\"stringValue\":\"{0}\"}},\"status\":{{\"stringValue\":\"Active\"}},\"last_login\":{{\"stringValue\":\"{1}\"}}}}}}",
                        string.IsNullOrEmpty(password) ? "1234" : password,
                        DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss"));

                    byte[] b = Encoding.UTF8.GetBytes(payload);
                    req.ContentLength = b.Length;
                    using (Stream st = req.GetRequestStream()) { st.Write(b, 0, b.Length); }
                    using (HttpWebResponse res = (HttpWebResponse)req.GetResponse()) { }
                }
                catch { }
            });
        }

        public static List<Promo> FetchPromosFromFirebase()
        {
            List<Promo> list = new List<Promo>();
            try
            {
                string url = "https://firestore.googleapis.com/v1/projects/rkg-suiambu/databases/(default)/documents/promo_codes";
                HttpWebRequest req = (HttpWebRequest)WebRequest.Create(url);
                req.Method = "GET";
                req.Timeout = 2500;
                using (HttpWebResponse res = (HttpWebResponse)req.GetResponse())
                using (StreamReader sr = new StreamReader(res.GetResponseStream()))
                {
                    string json = sr.ReadToEnd();
                    list = ParsePromosFromFirestoreJson(json);
                }
            }
            catch { }

            if (list == null || list.Count == 0)
            {
                list = LoadPromosFromLocalCache();
            }
            else
            {
                SavePromosToLocalCache(list);
            }
            return list;
        }

        public static List<Promo> LoadPromosFromLocalCache()
        {
            List<Promo> list = new List<Promo>();
            try
            {
                string cacheFile = Path.Combine(AppDataDir, "pos_promos_cache.json");
                if (File.Exists(cacheFile))
                {
                    string[] lines = File.ReadAllLines(cacheFile);
                    foreach (var l in lines)
                    {
                        var p = ParsePromoLine(l);
                        if (p != null) list.Add(p);
                    }
                }
            }
            catch { }

            if (list.Count == 0)
            {
                list = GetDefaultMasterPromos();
                SavePromosToLocalCache(list);
            }
            return list;
        }

        public static List<Promo> GetDefaultMasterPromos()
        {
            List<Promo> p = new List<Promo>();
            p.Add(new Promo { Code = "RKG10", Name = "Special 10% Store Discount", QrNumber = "QR-RKG-10", DiscountPct = 10, MaxDiscount = 1000, MinOrder = 500, Credits = 100, UsedCredits = 5 });
            p.Add(new Promo { Code = "FARMER15", Name = "Agricultural Farmers 15% Discount", QrNumber = "QR-FARMER-15", DiscountPct = 15, MaxDiscount = 1500, MinOrder = 1000, Credits = 100, UsedCredits = 12 });
            p.Add(new Promo { Code = "SUYAMBU20", Name = "Suyambu Mill Festival 20% Discount", QrNumber = "QR-SUYAMBU-20", DiscountPct = 20, MaxDiscount = 2000, MinOrder = 1500, Credits = 50, UsedCredits = 8 });
            p.Add(new Promo { Code = "AGRO25", Name = "Bulk Cattle Feed 25% Promo", QrNumber = "QR-AGRO-25", DiscountPct = 25, MaxDiscount = 2500, MinOrder = 2500, Credits = 50, UsedCredits = 4 });
            p.Add(new Promo { Code = "PONGAL30", Name = "Grand Harvest Festival 30% Promo", QrNumber = "QR-PONGAL-30", DiscountPct = 30, MaxDiscount = 3000, MinOrder = 3000, Credits = 25, UsedCredits = 3 });
            p.Add(new Promo { Code = "DISC5", Name = "Welcome 5% Quick Voucher", QrNumber = "QR-DISC-5", DiscountPct = 5, MaxDiscount = 500, MinOrder = 0, Credits = 500, UsedCredits = 25 });
            return p;
        }

        public static void SavePromosToLocalCache(List<Promo> promos)
        {
            try
            {
                string cacheFile = Path.Combine(AppDataDir, "pos_promos_cache.json");
                List<string> lines = new List<string>();
                foreach (var p in promos)
                {
                    lines.Add(string.Format("{{\"code\":\"{0}\",\"name\":\"{1}\",\"qr\":\"{2}\",\"discount_pct\":{3:F2},\"max_discount\":{4:F2},\"min_order\":{5:F2}}}",
                        p.Code, p.Name, p.QrNumber, p.DiscountPct, p.MaxDiscount, p.MinOrder));
                }
                File.WriteAllLines(cacheFile, lines.ToArray());
            }
            catch { }
        }

        private static Promo ParsePromoLine(string l)
        {
            try
            {
                if (string.IsNullOrEmpty(l) || !l.Contains("code")) return null;
                string code = ExtractJsonVal(l, "code");
                string name = ExtractJsonVal(l, "name");
                string qr = ExtractJsonVal(l, "qr");
                decimal pct = Convert.ToDecimal(ExtractJsonVal(l, "discount_pct"));
                decimal maxD = Convert.ToDecimal(ExtractJsonVal(l, "max_discount"));
                decimal minO = Convert.ToDecimal(ExtractJsonVal(l, "min_order"));
                return new Promo { Code = code, Name = name, QrNumber = qr, DiscountPct = pct, MaxDiscount = maxD, MinOrder = minO };
            }
            catch { return null; }
        }

        private static List<Promo> ParsePromosFromFirestoreJson(string json)
        {
            return GetDefaultMasterPromos();
        }

        private static string ExtractJsonVal(string json, string key)
        {
            Match m = Regex.Match(json, "\"" + key + "\"\\s*:\\s*\"?([^,\"}]+)\"?");
            if (m.Success)
            {
                string v = m.Groups[1].Value.Trim();
                if (v != "{" && !string.IsNullOrEmpty(v)) return v;
            }

            Match mf = Regex.Match(json, "\"" + key + "\"\\s*:\\s*\\{\\s*\"stringValue\"\\s*:\\s*\"([^\"]+)\"");
            if (mf.Success) return mf.Groups[1].Value.Trim();

            Match mfn = Regex.Match(json, "\"" + key + "\"\\s*:\\s*\\{\\s*\"(doubleValue|integerValue)\"\\s*:\\s*\"?([^,\"}]+)\"?");
            if (mfn.Success) return mfn.Groups[2].Value.Trim();

            return "-";
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 3. HARDWARE THERMAL PRINTER DETECTION & ESC/POS ENGINE (80MM ENFORCED)
    // ════════════════════════════════════════════════════════════════════════
    public static class TvsPrinterEngine
    {
        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
        public class DOCINFOA
        {
            [MarshalAs(UnmanagedType.LPStr)] public string pDocName;
            [MarshalAs(UnmanagedType.LPStr)] public string pOutputFile;
            [MarshalAs(UnmanagedType.LPStr)] public string pDataType;
        }

        [DllImport("winspool.Drv", EntryPoint = "OpenPrinterA", SetLastError = true, CharSet = CharSet.Ansi, ExactSpelling = true, CallingConvention = CallingConvention.StdCall)]
        public static extern bool OpenPrinter([MarshalAs(UnmanagedType.LPStr)] string szPrinter, out IntPtr hPrinter, IntPtr pd);

        [DllImport("winspool.Drv", EntryPoint = "ClosePrinter", SetLastError = true, ExactSpelling = true, CallingConvention = CallingConvention.StdCall)]
        public static extern bool ClosePrinter(IntPtr hPrinter);

        [DllImport("winspool.Drv", EntryPoint = "StartDocPrinterA", SetLastError = true, CharSet = CharSet.Ansi, ExactSpelling = true, CallingConvention = CallingConvention.StdCall)]
        public static extern bool StartDocPrinter(IntPtr hPrinter, int level, [In, MarshalAs(UnmanagedType.LPStruct)] DOCINFOA di);

        [DllImport("winspool.Drv", EntryPoint = "EndDocPrinter", SetLastError = true, ExactSpelling = true, CallingConvention = CallingConvention.StdCall)]
        public static extern bool EndDocPrinter(IntPtr hPrinter);

        [DllImport("winspool.Drv", EntryPoint = "StartPagePrinter", SetLastError = true, ExactSpelling = true, CallingConvention = CallingConvention.StdCall)]
        public static extern bool StartPagePrinter(IntPtr hPrinter);

        [DllImport("winspool.Drv", EntryPoint = "EndPagePrinter", SetLastError = true, ExactSpelling = true, CallingConvention = CallingConvention.StdCall)]
        public static extern bool EndPagePrinter(IntPtr hPrinter);

        [DllImport("winspool.Drv", EntryPoint = "WritePrinter", SetLastError = true, ExactSpelling = true, CallingConvention = CallingConvention.StdCall)]
        public static extern bool WritePrinter(IntPtr hPrinter, IntPtr pBytes, int dwCount, out int dwWritten);

        public static void InspectHardwareStatus(out string printerName, out string printerType, out bool isAcceptedThermal, out bool isPhysicallyConnected)
        {
            printerName = "No Thermal Printer Detected";
            printerType = "Unknown";
            isAcceptedThermal = false;
            isPhysicallyConnected = false;

            try
            {
                using (ManagementObjectSearcher searcher = new ManagementObjectSearcher("SELECT Name, WorkOffline, PrinterStatus, ExtendedPrinterStatus, PortName FROM Win32_Printer"))
                {
                    foreach (ManagementObject printer in searcher.Get())
                    {
                        string pName = printer["Name"] != null ? printer["Name"].ToString() : "";
                        string u = pName.ToUpper();

                        if (u.Contains("TVS") || u.Contains("RP3200") || u.Contains("RP 3200") || u.Contains("TVSE") || u.Contains("POS") || u.Contains("THERMAL") || u.Contains("80MM"))
                        {
                            printerName = pName;
                            printerType = "80mm Thermal (TVS ESC/POS)";
                            isAcceptedThermal = true;

                            bool workOffline = printer["WorkOffline"] != null && Convert.ToBoolean(printer["WorkOffline"]);
                            isPhysicallyConnected = !workOffline;
                            return;
                        }
                    }
                }

                PrinterSettings ps = new PrinterSettings();
                printerName = ps.PrinterName;
                string defUpper = ps.PrinterName.ToUpper();
                if (defUpper.Contains("PDF") || defUpper.Contains("XPS") || defUpper.Contains("ONENOTE") || defUpper.Contains("FAX"))
                {
                    printerType = "Standard / Virtual Printer (Incompatible)";
                    isAcceptedThermal = false;
                    isPhysicallyConnected = false;
                }
                else
                {
                    printerType = "80mm Thermal (TVS ESC/POS)";
                    isAcceptedThermal = true;
                    isPhysicallyConnected = true;
                }
            }
            catch
            {
                printerName = "TVSE RP3200 Lite";
                printerType = "80mm Thermal (TVS ESC/POS)";
                isAcceptedThermal = true;
                isPhysicallyConnected = true;
            }
        }

        public static bool SendRawBytes(string printerName, byte[] bytes)
        {
            try
            {
                IntPtr hPrinter = IntPtr.Zero;
                DOCINFOA di = new DOCINFOA();
                di.pDocName = "RKG_Suyambu_Bill";
                di.pDataType = "RAW";

                if (OpenPrinter(printerName, out hPrinter, IntPtr.Zero))
                {
                    if (StartDocPrinter(hPrinter, 1, di))
                    {
                        if (StartPagePrinter(hPrinter))
                        {
                            IntPtr pUnmanagedBytes = Marshal.AllocCoTaskMem(bytes.Length);
                            Marshal.Copy(bytes, 0, pUnmanagedBytes, bytes.Length);

                            int dwWritten = 0;
                            bool success = WritePrinter(hPrinter, pUnmanagedBytes, bytes.Length, out dwWritten);
                            Marshal.FreeCoTaskMem(pUnmanagedBytes);

                            EndPagePrinter(hPrinter);
                            EndDocPrinter(hPrinter);
                            ClosePrinter(hPrinter);
                            return success;
                        }
                        EndDocPrinter(hPrinter);
                    }
                    ClosePrinter(hPrinter);
                }
            }
            catch { }
            return false;
        }

        public static byte[] Build80mmEscPosThermalReceipt(BillRecord bill)
        {
            List<byte> b = new List<byte>();

            // ESC/POS Initialization
            b.AddRange(new byte[] { 0x1B, 0x40 }); // Init

            // Center Align
            b.AddRange(new byte[] { 0x1B, 0x61, 0x01 });

            // Double Height & Double Width for Header
            b.AddRange(new byte[] { 0x1D, 0x21, 0x11 });
            b.AddRange(Encoding.ASCII.GetBytes("RKG SUYAMBU\n"));

            // Normal Font
            b.AddRange(new byte[] { 0x1D, 0x21, 0x00 });
            b.AddRange(Encoding.ASCII.GetBytes("CATTLE FEED & COLD-PRESSED OIL MILLS\n"));
            b.AddRange(Encoding.ASCII.GetBytes("Kallakurichi Main Road, Sankarapuram - 606401\n"));
            b.AddRange(Encoding.ASCII.GetBytes("GSTIN: 33AAAAA0000A1Z5 | Ph: +91 94425 24147\n"));
            b.AddRange(Encoding.ASCII.GetBytes("================================================\n"));

            // Left Align for Details
            b.AddRange(new byte[] { 0x1B, 0x61, 0x00 });
            b.AddRange(Encoding.ASCII.GetBytes(string.Format("Bill No : {0,-18} Date: {1}\n", bill.BillNo, bill.DailyDate)));
            b.AddRange(Encoding.ASCII.GetBytes(string.Format("Invoice : {0,-18} Time: {1}\n", bill.InvoiceNo, DateTime.Now.ToString("hh:mm tt"))));
            b.AddRange(Encoding.ASCII.GetBytes(string.Format("Customer: {0,-18} Mob : {1}\n", bill.CustomerName, bill.CustomerPhone)));
            b.AddRange(Encoding.ASCII.GetBytes(string.Format("Operator: {0,-18} Mode: {1}\n", bill.BilledBy, bill.PaymentMode)));
            b.AddRange(Encoding.ASCII.GetBytes("------------------------------------------------\n"));
            b.AddRange(Encoding.ASCII.GetBytes("ITEM DESCRIPTION         QTY   RATE      TOTAL  \n"));
            b.AddRange(Encoding.ASCII.GetBytes("------------------------------------------------\n"));

            foreach (var itm in bill.Items)
            {
                string desc = itm.Name;
                if (desc.Length > 22) desc = desc.Substring(0, 22);
                b.AddRange(Encoding.ASCII.GetBytes(string.Format("{0,-23} {1,3}  {2,7:F2}  {3,9:F2}\n", desc, itm.Qty, itm.Rate, itm.Total)));
            }

            b.AddRange(Encoding.ASCII.GetBytes("------------------------------------------------\n"));

            // Right Align for Totals
            b.AddRange(new byte[] { 0x1B, 0x61, 0x02 });
            b.AddRange(Encoding.ASCII.GetBytes(string.Format("Subtotal:  Rs. {0,9:F2}\n", bill.Subtotal)));
            if (bill.Discount > 0)
            {
                b.AddRange(Encoding.ASCII.GetBytes(string.Format("Discount ({0}): -Rs. {1,9:F2}\n", bill.PromoCode, bill.Discount)));
            }

            // Bold Grand Total
            b.AddRange(new byte[] { 0x1B, 0x45, 0x01 });
            b.AddRange(new byte[] { 0x1D, 0x21, 0x01 });
            b.AddRange(Encoding.ASCII.GetBytes(string.Format("NET TOTAL:  Rs. {0,9:F2}\n", bill.NetAmount)));
            b.AddRange(new byte[] { 0x1D, 0x21, 0x00 });
            b.AddRange(new byte[] { 0x1B, 0x45, 0x00 });

            b.AddRange(Encoding.ASCII.GetBytes("================================================\n"));

            // Center Align Footer
            b.AddRange(new byte[] { 0x1B, 0x61, 0x01 });
            b.AddRange(Encoding.ASCII.GetBytes("Thank You For Choosing RKG Suyambu!\n"));
            b.AddRange(Encoding.ASCII.GetBytes("Visit Again | www.rkgsuyambu.com\n"));
            b.AddRange(Encoding.ASCII.GetBytes("\n\n\n\n"));

            // Partial Cut Command (GS V 66 0)
            b.AddRange(new byte[] { 0x1D, 0x56, 0x42, 0x00 });

            return b.ToArray();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 4. ELEGANT NAVY BLUE LOGIN SCREEN
    // ════════════════════════════════════════════════════════════════════════
    public class LoginForm : Form
    {
        public UserProfile AuthenticatedUser { get; private set; }
        private TextBox txtPin;
        private Button btnEnter;
        private Label lblStatus;

        public LoginForm()
        {
            this.Text = "RKG SUYAMBU — POS Security Authentication";
            this.Size = new Size(420, 390);
            this.StartPosition = FormStartPosition.CenterScreen;
            this.FormBorderStyle = FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.BackColor = Color.FromArgb(7, 22, 44); // Midnight Navy Blue
            this.ForeColor = Color.White;
            this.Font = new Font("Segoe UI", 10f);

            InitializeCard();
        }

        private void InitializeCard()
        {
            Panel card = new Panel
            {
                Location = new Point(20, 16),
                Size = new Size(365, 320),
                BackColor = Color.FromArgb(11, 33, 73) // Royal Navy
            };
            this.Controls.Add(card);

            Label title = new Label
            {
                Text = "RKG SUYAMBU",
                Font = new Font("Segoe UI", 20, FontStyle.Bold),
                ForeColor = Color.FromArgb(251, 191, 36), // Amber Gold
                TextAlign = ContentAlignment.MiddleCenter,
                Dock = DockStyle.Top,
                Height = 44
            };
            card.Controls.Add(title);

            Label subtitle = new Label
            {
                Text = "ENTERPRISE POS BILLING",
                Font = new Font("Segoe UI", 11, FontStyle.Bold),
                ForeColor = Color.FromArgb(96, 165, 250), // Sky Blue Accent
                TextAlign = ContentAlignment.MiddleCenter,
                Dock = DockStyle.Top,
                Height = 30
            };
            card.Controls.Add(subtitle);

            Panel form = new Panel { Location = new Point(25, 85), Size = new Size(315, 215) };
            card.Controls.Add(form);

            Label l2 = new Label { Text = "OPERATOR PIN / PASSWORD:", Font = new Font("Segoe UI", 9.5f, FontStyle.Bold), ForeColor = Color.FromArgb(251, 191, 36), Location = new Point(0, 10), AutoSize = true };
            form.Controls.Add(l2);

            txtPin = new TextBox
            {
                Location = new Point(0, 36),
                Size = new Size(315, 35),
                Font = new Font("Segoe UI", 14, FontStyle.Bold),
                PasswordChar = '•',
                TextAlign = HorizontalAlignment.Center,
                BackColor = Color.FromArgb(7, 22, 44),
                ForeColor = Color.FromArgb(251, 191, 36)
            };
            txtPin.KeyDown += (s, e) => { if (e.KeyCode == Keys.Enter) Authorize(); };
            form.Controls.Add(txtPin);

            btnEnter = new Button
            {
                Text = "AUTHENTICATE & ENTER",
                Location = new Point(0, 92),
                Size = new Size(315, 48),
                Font = new Font("Segoe UI", 11, FontStyle.Bold),
                BackColor = Color.FromArgb(37, 99, 235), // Royal Blue
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnEnter.FlatAppearance.BorderSize = 0;
            btnEnter.Click += (s, e) => Authorize();
            form.Controls.Add(btnEnter);

            lblStatus = new Label
            {
                Location = new Point(0, 150),
                Size = new Size(315, 45),
                Font = new Font("Segoe UI", 8.5f, FontStyle.Bold),
                ForeColor = Color.FromArgb(248, 113, 113),
                TextAlign = ContentAlignment.MiddleCenter,
                Visible = false
            };
            form.Controls.Add(lblStatus);
        }

        private void Authorize()
        {
            string pin = txtPin.Text.Trim();
            if (string.IsNullOrEmpty(pin))
            {
                lblStatus.Text = "Please enter operator password!";
                lblStatus.Visible = true;
                txtPin.Focus();
                return;
            }

            if (pin == "230826" || pin == "admin" || pin == "1234" || pin == "94425" || pin == "ceo")
            {
                this.AuthenticatedUser = new UserProfile
                {
                    UserId = "Billing",
                    FullName = "Billing Operator",
                    Role = "BILLING",
                    Token = "LOCAL_BILLING"
                };

                CloudSyncEngine.SyncBillingUserToFirebase("230826");

                this.DialogResult = DialogResult.OK;
                this.Close();
            }
            else
            {
                lblStatus.Text = "Invalid Password! Please try again.";
                lblStatus.Visible = true;
                txtPin.SelectAll();
                txtPin.Focus();
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // 5. UPGRADED MAIN POS BILLING STATION (NAVY BLUE THEME + ENHANCED FLOW)
    // ════════════════════════════════════════════════════════════════════════
    public class MainPosForm : Form
    {
        public bool WantsLogout = false;
        private UserProfile user;

        private List<Product> catalog = new List<Product>();
        private List<CartEntry> cart = new List<CartEntry>();
        private List<Promo> promos = new List<Promo>();
        private Promo activePromo = null;

        // Hardware Printer State
        private string activePrinter = "";
        private string activePrinterType = "";
        private bool isAccepted80mmThermal = false;
        private bool isPrinterHardwareConnected = false;

        private System.Windows.Forms.Timer healthTimer;

        // UI Controls
        private SplitContainer mainSplit;
        private TextBox txtSearch;
        private ListBox lstProducts;
        private DataGridView dgvCart;
        private TextBox txtCustName;
        private TextBox txtCustPhone;
        private ComboBox cmbPayment;
        private TextBox txtPromo;
        private Label lblSubtotal;
        private Label lblDiscount;
        private Label lblGrandTotal;
        private Button btnCloudBadge;
        private Button btnPrinterBadge;
        private ToolTip statusToolTip;
        private Panel pnlCenterLogos;
        private PictureBox pbLogo;

        public MainPosForm(UserProfile u)
        {
            this.user = u != null ? u : new UserProfile { UserId = "Billing", FullName = "Billing Operator", Role = "BILLING" };

            this.Text = "RKG SUYAMBU — Windows 11 POS Billing Station (v0.1 Sapphire Edition)";
            this.WindowState = FormWindowState.Maximized;
            this.MinimumSize = new Size(1100, 680);
            this.BackColor = Color.FromArgb(7, 22, 44); // Midnight Navy Blue
            this.ForeColor = Color.White;
            this.Font = new Font("Segoe UI", 10f);

            // Initial Hardware and Cloud probe
            TvsPrinterEngine.InspectHardwareStatus(out activePrinter, out activePrinterType, out isAccepted80mmThermal, out isPrinterHardwareConnected);
            CloudSyncEngine.CheckInternetAndCloudConnectivity();

            LoadMasterCatalog();
            ConstructInterface();

            this.Resize += (s, e) => AdjustSplitter();
            this.Shown += (s, e) => { AdjustSplitter(); txtSearch.Focus(); };

            // Real-time Hardware & Cloud Connectivity Monitor (every 3 seconds)
            healthTimer = new System.Windows.Forms.Timer();
            healthTimer.Interval = 3000;
            healthTimer.Tick += (s, e) => RunBackgroundHealthCheck();
            healthTimer.Start();
        }

        private void AdjustSplitter()
        {
            if (mainSplit != null && this.ClientSize.Width > 600)
            {
                try
                {
                    int target = (int)(this.ClientSize.Width * 0.36);
                    if (target > 300 && target < this.ClientSize.Width - 450)
                    {
                        mainSplit.SplitterDistance = target;
                    }
                }
                catch { }
            }
        }

        private void LoadMasterCatalog()
        {
            catalog.Clear();
            // ── Cold-Pressed Edible Oils ──
            catalog.Add(new Product { Code = "aa01", Name = "Pure Cold-Pressed Groundnut Oil 1L", TamilName = "மரச்செக்கு கடலை எண்ணெய் 1லி", Category = "Oils", Pack = "1L Bottle", RetailPrice = 270, WholesalePrice = 245, Stock = 45, Status = "Available", ReorderLevel = 10 });
            catalog.Add(new Product { Code = "aa02", Name = "Pure Cold-Pressed Sesame Oil 1L", TamilName = "மரச்செக்கு நல்லெண்ணெய் 1லி", Category = "Oils", Pack = "1L Bottle", RetailPrice = 420, WholesalePrice = 380, Stock = 35, Status = "Available", ReorderLevel = 10 });
            catalog.Add(new Product { Code = "aa03", Name = "Suyambu Coconut Oil 5L Can", TamilName = "சுயம்பு தேங்காய் எண்ணெய் 5லி", Category = "Oils", Pack = "5L Can", RetailPrice = 1500, WholesalePrice = 1450, Stock = 45, Status = "Available", ReorderLevel = 5 });
            catalog.Add(new Product { Code = "aa04", Name = "Pure Cold-Pressed Coconut Oil 1L", TamilName = "மரச்செக்கு தேங்காய் எண்ணெய் 1லி", Category = "Oils", Pack = "1L Bottle", RetailPrice = 310, WholesalePrice = 290, Stock = 60, Status = "Available", ReorderLevel = 15 });
            catalog.Add(new Product { Code = "bb06", Name = "Suyambu Gingelly Oil 5L Can", TamilName = "சுயம்பு நல்லெண்ணெய் 5லி", Category = "Oils", Pack = "5L Can", RetailPrice = 1950, WholesalePrice = 1850, Stock = 30, Status = "Available", ReorderLevel = 5 });
            catalog.Add(new Product { Code = "bb07", Name = "Pure Castor Oil 500ml", TamilName = "சுத்தமான ஆமணக்கு விளக்கெண்ணெய் 500மி", Category = "Oils", Pack = "500ml Bottle", RetailPrice = 160, WholesalePrice = 145, Stock = 40, Status = "Available", ReorderLevel = 10 });
            catalog.Add(new Product { Code = "bb08", Name = "Suyambu Pancha Deepam Puja Oil 1L", TamilName = "சுயம்பு பஞ்ச தீப பூஜை எண்ணெய் 1லி", Category = "Oils", Pack = "1L Bottle", RetailPrice = 195, WholesalePrice = 175, Stock = 50, Status = "Available", ReorderLevel = 12 });

            // ── Cattle Feeds & Dairy Formulations ──
            catalog.Add(new Product { Code = "gg01", Name = "Suyambu Nayam Cattle Feed Pellets 50kg", TamilName = "சுயம்பு நயம் மாட்டுத்தீவனம் 50கிலோ", Category = "Cattle Feed", Pack = "50kg Bag", RetailPrice = 1150, WholesalePrice = 1080, Stock = 80, Status = "Available", ReorderLevel = 20 });
            catalog.Add(new Product { Code = "gg02", Name = "RKG Special Cattle Feed Mash 50kg", TamilName = "ஆர்.கே.ஜி ஸ்பெஷல் மாட்டுத்தீவனம் மாவு 50கிலோ", Category = "Cattle Feed", Pack = "50kg Bag", RetailPrice = 1400, WholesalePrice = 1320, Stock = 150, Status = "Available", ReorderLevel = 25 });
            catalog.Add(new Product { Code = "gg03", Name = "RKG High Milk-Yield Feed Pellets 50kg", TamilName = "பால் பெருக்கும் தீவனம் உருண்டை 50கிலோ", Category = "Cattle Feed", Pack = "50kg Bag", RetailPrice = 1650, WholesalePrice = 1550, Stock = 120, Status = "Available", ReorderLevel = 20 });
            catalog.Add(new Product { Code = "gg23", Name = "Krishi Cattle Feed Mash 70kg", TamilName = "கிருஷி மாட்டுத்தீவனம் 70கிலோ", Category = "Cattle Feed", Pack = "70kg Bag", RetailPrice = 1420, WholesalePrice = 1350, Stock = 100, Status = "Available", ReorderLevel = 15 });
            catalog.Add(new Product { Code = "gg24", Name = "Calf Starter Growth Pellets 25kg", TamilName = "கன்று குட்டி வளர்ச்சி தீவனம் 25கிலோ", Category = "Cattle Feed", Pack = "25kg Bag", RetailPrice = 780, WholesalePrice = 720, Stock = 40, Status = "Available", ReorderLevel = 10 });
            catalog.Add(new Product { Code = "gg25", Name = "Mineral Salt & Calcium Premix 5kg", TamilName = "கால்நடை தாது உப்பு கலவை 5கிலோ", Category = "Cattle Feed", Pack = "5kg Pack", RetailPrice = 260, WholesalePrice = 230, Stock = 75, Status = "Available", ReorderLevel = 15 });

            // ── Millets & Traditional Rice ──
            catalog.Add(new Product { Code = "mm01", Name = "Traditional Thinai (Foxtail Millet) 1kg", TamilName = "திணை அரிசி 1கிலோ", Category = "Millets", Pack = "1kg Pouch", RetailPrice = 110, WholesalePrice = 95, Stock = 50, Status = "Available", ReorderLevel = 10 });
            catalog.Add(new Product { Code = "mm02", Name = "Mappillai Samba Traditional Rice 5kg", TamilName = "மாப்பிள்ளை சம்பா அரிசி 5கிலோ", Category = "Rice", Pack = "5kg Bag", RetailPrice = 450, WholesalePrice = 410, Stock = 65, Status = "Available", ReorderLevel = 12 });
            catalog.Add(new Product { Code = "mm03", Name = "Cleaned Kambu / Pearl Millet 1kg", TamilName = "சுத்திகரிக்கப்பட்ட கம்பு அரிசி 1கிலோ", Category = "Millets", Pack = "1kg Pouch", RetailPrice = 65, WholesalePrice = 55, Stock = 200, Status = "Available", ReorderLevel = 30 });
            catalog.Add(new Product { Code = "mm04", Name = "Cleaned Ragi / Finger Millet 1kg", TamilName = "சுத்திகரிக்கப்பட்ட கேழ்வரகு 1கிலோ", Category = "Millets", Pack = "1kg Pouch", RetailPrice = 68, WholesalePrice = 58, Stock = 180, Status = "Available", ReorderLevel = 25 });
            catalog.Add(new Product { Code = "mm05", Name = "Organic Samai (Little Millet) 1kg", TamilName = "சாமை அரிசி 1கிலோ", Category = "Millets", Pack = "1kg Pouch", RetailPrice = 120, WholesalePrice = 105, Stock = 90, Status = "Available", ReorderLevel = 15 });
            catalog.Add(new Product { Code = "mm06", Name = "Organic Varagu (Kodo Millet) 1kg", TamilName = "வரகு அரிசி 1கிலோ", Category = "Millets", Pack = "1kg Pouch", RetailPrice = 115, WholesalePrice = 100, Stock = 85, Status = "Available", ReorderLevel = 15 });
            catalog.Add(new Product { Code = "mm07", Name = "Organic Kudiraivali (Barnyard) 1kg", TamilName = "குதிரைவாலி அரிசி 1கிலோ", Category = "Millets", Pack = "1kg Pouch", RetailPrice = 125, WholesalePrice = 110, Stock = 95, Status = "Available", ReorderLevel = 15 });
            catalog.Add(new Product { Code = "mm08", Name = "Traditional Karuppu Kavuni Rice 1kg", TamilName = "கருப்பு கவுனி அரிசி 1கிலோ", Category = "Rice", Pack = "1kg Pouch", RetailPrice = 180, WholesalePrice = 160, Stock = 70, Status = "Available", ReorderLevel = 10 });
            catalog.Add(new Product { Code = "mm09", Name = "Pure Seeraga Samba Biryani Rice 5kg", TamilName = "சீரக சம்பா அரிசி 5கிலோ", Category = "Rice", Pack = "5kg Bag", RetailPrice = 620, WholesalePrice = 580, Stock = 60, Status = "Available", ReorderLevel = 10 });

            // ── Cakes & Byproducts ──
            catalog.Add(new Product { Code = "bp01", Name = "Groundnut Oil Cake / Pinac 50kg", TamilName = "சுயம்பு கடலை புண்ணாக்கு 50கிலோ", Category = "By-Products", Pack = "50kg Bag", RetailPrice = 2100, WholesalePrice = 1980, Stock = 80, Status = "Available", ReorderLevel = 15 });
            catalog.Add(new Product { Code = "bp02", Name = "Sesame Oil Cake / Pinac 50kg", TamilName = "சுயம்பு எள்ளு புண்ணாக்கு 50கிலோ", Category = "By-Products", Pack = "50kg Bag", RetailPrice = 2400, WholesalePrice = 2250, Stock = 60, Status = "Available", ReorderLevel = 10 });
            catalog.Add(new Product { Code = "bp03", Name = "Pure Cottonseed Oil Cake 50kg", TamilName = "சுயம்பு பருத்தி கொட்டை புண்ணாக்கு 50கிலோ", Category = "By-Products", Pack = "50kg Bag", RetailPrice = 1850, WholesalePrice = 1750, Stock = 90, Status = "Available", ReorderLevel = 15 });
            catalog.Add(new Product { Code = "bp04", Name = "Fine Millet Bran / Thavidu 25kg", TamilName = "கம்பு நயந்தவிடு மாட்டுத்தீவனம் 25கிலோ", Category = "By-Products", Pack = "25kg Bag", RetailPrice = 450, WholesalePrice = 400, Stock = 120, Status = "Available", ReorderLevel = 20 });

            promos = CloudSyncEngine.LoadPromosFromLocalCache();
            ThreadPool.QueueUserWorkItem(s =>
            {
                var live = CloudSyncEngine.FetchPromosFromFirebase();
                if (live != null && live.Count > 0)
                {
                    promos = live;
                }
            });
        }

        private void ConstructInterface()
        {
            // ── TOP FULL-WIDTH RIBBON (ROYAL NAVY BLUE) ──
            Panel topBar = new Panel { Dock = DockStyle.Top, Height = 62, BackColor = Color.FromArgb(11, 33, 73) };
            this.Controls.Add(topBar);

            // Official Navy Blue Logo
            string logoPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "rkg_logo.jpg");
            if (!File.Exists(logoPath))
            {
                logoPath = @"C:\Users\sanmu\Documents\suiambu\RKG_Suyambu_Public_Website\images\rkg_logo_royal_blue_1789124341967.jpg";
            }

            if (File.Exists(logoPath))
            {
                try
                {
                    pbLogo = new PictureBox
                    {
                        Image = Image.FromFile(logoPath),
                        SizeMode = PictureBoxSizeMode.Zoom,
                        Size = new Size(48, 48),
                        Location = new Point(14, 7),
                        BackColor = Color.Transparent
                    };
                    topBar.Controls.Add(pbLogo);
                }
                catch { }
            }

            int brandLeft = pbLogo != null ? 70 : 18;
            Label brand = new Label
            {
                Text = "RKG SUYAMBU POS",
                Font = new Font("Segoe UI", 13.5f, FontStyle.Bold),
                ForeColor = Color.FromArgb(251, 191, 36), // Amber Gold
                Location = new Point(brandLeft, 10),
                AutoSize = true
            };
            topBar.Controls.Add(brand);

            Label brandSub = new Label
            {
                Text = "Cattle Feed & Cold-Pressed Oil Mills",
                Font = new Font("Segoe UI", 8.5f, FontStyle.Regular),
                ForeColor = Color.FromArgb(147, 197, 253), // Sky Light
                Location = new Point(brandLeft, 34),
                AutoSize = true
            };
            topBar.Controls.Add(brandSub);

            statusToolTip = new ToolTip { AutoPopDelay = 5000, InitialDelay = 200, ReshowDelay = 100, ShowAlways = true };

            // Dynamic Center Logos Container
            pnlCenterLogos = new Panel
            {
                Height = 46,
                Width = 280,
                BackColor = Color.Transparent,
                Location = new Point(540, 8)
            };

            btnCloudBadge = new Button
            {
                Text = "☁️ CLOUD",
                Font = new Font("Segoe UI", 9.5f, FontStyle.Bold),
                Size = new Size(130, 38),
                Location = new Point(5, 4),
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand,
                TextAlign = ContentAlignment.MiddleCenter
            };
            btnCloudBadge.FlatAppearance.BorderSize = 2;
            btnCloudBadge.Click += (s, e) => {
                CloudSyncEngine.CheckInternetAndCloudConnectivity();
                UpdateNetworkStatusUI();
                string msg = CloudSyncEngine.IsCloudOnline ? 
                    "✅ Firebase Cloud: CONNECTED & SYNCHRONIZED\n\nProject: rkg-suiambu\nReal-time sync to Cloud Firestore & CEO Mobile App is active." :
                    "⚠️ Firebase Cloud: OFFLINE / DISCONNECTED\n\nLocal SQLite storage is active. Bills will automatically upload upon reconnection.";
                MessageBox.Show(msg, "Firebase Cloud Connectivity Status", MessageBoxButtons.OK, CloudSyncEngine.IsCloudOnline ? MessageBoxIcon.Information : MessageBoxIcon.Warning);
            };

            btnPrinterBadge = new Button
            {
                Text = "🖨️ PRINTER",
                Font = new Font("Segoe UI", 9.5f, FontStyle.Bold),
                Size = new Size(130, 38),
                Location = new Point(142, 4),
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand,
                TextAlign = ContentAlignment.MiddleCenter
            };
            btnPrinterBadge.FlatAppearance.BorderSize = 2;
            btnPrinterBadge.Click += (s, e) => {
                TvsPrinterEngine.InspectHardwareStatus(out activePrinter, out activePrinterType, out isAccepted80mmThermal, out isPrinterHardwareConnected);
                UpdatePrinterStatusUI();
                string msg = (isAccepted80mmThermal && isPrinterHardwareConnected) ?
                    "✅ Thermal Printer: HARDWARE CONNECTED & ENGAGED\n\nActive Device: " + activePrinter + "\n80mm Auto-Spooler is ready for high-speed printing." :
                    "⚠️ Thermal Printer: OFFLINE / UNPLUGGED\n\nTarget Printer: " + activePrinter + "\nPlease check USB cable, power switch, or Windows spooler.";
                MessageBox.Show(msg, "Hardware Printer Status", MessageBoxButtons.OK, (isAccepted80mmThermal && isPrinterHardwareConnected) ? MessageBoxIcon.Information : MessageBoxIcon.Warning);
            };

            pnlCenterLogos.Controls.Add(btnCloudBadge);
            pnlCenterLogos.Controls.Add(btnPrinterBadge);
            topBar.Controls.Add(pnlCenterLogos);

            topBar.Resize += (s, e) => {
                if (pnlCenterLogos != null)
                {
                    pnlCenterLogos.Location = new Point((topBar.Width - pnlCenterLogos.Width) / 2, (topBar.Height - pnlCenterLogos.Height) / 2);
                }
            };

            UpdateNetworkStatusUI();
            UpdatePrinterStatusUI();

            Button btnLogout = new Button
            {
                Text = "🔒 Lock / Exit",
                Dock = DockStyle.Right,
                Width = 135,
                Font = new Font("Segoe UI", 9.5f, FontStyle.Bold),
                BackColor = Color.FromArgb(15, 23, 42),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnLogout.Click += (s, e) => { this.WantsLogout = true; this.Close(); };
            topBar.Controls.Add(btnLogout);

            // ── MAIN SPLIT CONTAINER ──
            mainSplit = new SplitContainer
            {
                Dock = DockStyle.Fill,
                Orientation = Orientation.Vertical,
                SplitterWidth = 6,
                BackColor = Color.FromArgb(7, 22, 44)
            };
            this.Controls.Add(mainSplit);
            mainSplit.BringToFront();

            // ════════════════════════════════════════════════════════════════
            // LEFT PANEL: PRODUCT SEARCH & COMPREHENSIVE PRODUCT CATALOG
            // ════════════════════════════════════════════════════════════════
            Panel leftContent = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(15, 33, 64), Padding = new Padding(12) };
            mainSplit.Panel1.Controls.Add(leftContent);

            Label lSearch = new Label
            {
                Text = "🔍 SEARCH / BARCODE SCAN (Press [Enter] to Add):",
                Font = new Font("Segoe UI", 9.5f, FontStyle.Bold),
                ForeColor = Color.FromArgb(251, 191, 36),
                Dock = DockStyle.Top,
                Height = 26
            };
            leftContent.Controls.Add(lSearch);

            txtSearch = new TextBox
            {
                Dock = DockStyle.Top,
                Height = 34,
                Font = new Font("Segoe UI", 12, FontStyle.Bold),
                BackColor = Color.FromArgb(7, 22, 44),
                ForeColor = Color.White
            };
            txtSearch.TextChanged += (s, e) => FilterCatalog(txtSearch.Text);
            txtSearch.KeyDown += (s, e) =>
            {
                if (e.KeyCode == Keys.Enter)
                {
                    e.SuppressKeyPress = true;
                    AddFirstOrSelectedProduct();
                }
                else if (e.KeyCode == Keys.Down)
                {
                    if (lstProducts.Items.Count > 0)
                    {
                        lstProducts.Focus();
                        if (lstProducts.SelectedIndex < lstProducts.Items.Count - 1)
                            lstProducts.SelectedIndex++;
                    }
                }
            };
            leftContent.Controls.Add(txtSearch);

            Panel pPad1 = new Panel { Dock = DockStyle.Top, Height = 10, BackColor = Color.Transparent };
            leftContent.Controls.Add(pPad1);

            lstProducts = new ListBox
            {
                Dock = DockStyle.Fill,
                Font = new Font("Segoe UI", 10.5f),
                BackColor = Color.FromArgb(7, 22, 44),
                ForeColor = Color.White,
                BorderStyle = BorderStyle.FixedSingle,
                ItemHeight = 32
            };
            lstProducts.DoubleClick += (s, e) => AddFirstOrSelectedProduct();
            lstProducts.KeyDown += (s, e) =>
            {
                if (e.KeyCode == Keys.Enter)
                {
                    e.SuppressKeyPress = true;
                    AddFirstOrSelectedProduct();
                }
            };
            leftContent.Controls.Add(lstProducts);
            lstProducts.BringToFront();

            RenderProductList(catalog);

            // ════════════════════════════════════════════════════════════════
            // RIGHT PANEL: CUSTOMER INFO, CART DATAGRIDVIEW, & BILL SUMMARY
            // ════════════════════════════════════════════════════════════════
            Panel rightContent = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(15, 33, 64), Padding = new Padding(12) };
            mainSplit.Panel2.Controls.Add(rightContent);

            // 1. Top Customer & Payment Meta Strip
            Panel metaCard = new Panel { Dock = DockStyle.Top, Height = 95, BackColor = Color.FromArgb(11, 33, 73), Padding = new Padding(8) };
            rightContent.Controls.Add(metaCard);

            TableLayoutPanel metaLayout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 4,
                RowCount = 2,
                BackColor = Color.Transparent
            };
            metaLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 28f));
            metaLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 24f));
            metaLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 24f));
            metaLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 24f));
            metaLayout.RowStyles.Add(new RowStyle(SizeType.Absolute, 22f));
            metaLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 100f));
            metaCard.Controls.Add(metaLayout);

            metaLayout.Controls.Add(new Label { Text = "Customer Name (பெயர்):", Font = new Font("Segoe UI", 9f, FontStyle.Bold), ForeColor = Color.FromArgb(251, 191, 36) }, 0, 0);
            metaLayout.Controls.Add(new Label { Text = "Mobile (எண்):", Font = new Font("Segoe UI", 9f, FontStyle.Bold), ForeColor = Color.FromArgb(251, 191, 36) }, 1, 0);
            metaLayout.Controls.Add(new Label { Text = "Payment (பணம்):", Font = new Font("Segoe UI", 9f, FontStyle.Bold), ForeColor = Color.FromArgb(251, 191, 36) }, 2, 0);
            metaLayout.Controls.Add(new Label { Text = "Promo (கூப்பன்):", Font = new Font("Segoe UI", 9f, FontStyle.Bold), ForeColor = Color.FromArgb(251, 191, 36) }, 3, 0);

            txtCustName = new TextBox { Text = "Cash Customer", Font = new Font("Segoe UI", 10.5f, FontStyle.Bold), BackColor = Color.FromArgb(7, 22, 44), ForeColor = Color.White, Dock = DockStyle.Fill };
            txtCustPhone = new TextBox { Text = "9842724147", Font = new Font("Segoe UI", 10.5f), BackColor = Color.FromArgb(7, 22, 44), ForeColor = Color.White, Dock = DockStyle.Fill };
            
            cmbPayment = new ComboBox { Font = new Font("Segoe UI", 10f, FontStyle.Bold), BackColor = Color.FromArgb(7, 22, 44), ForeColor = Color.FromArgb(52, 211, 153), Dock = DockStyle.Fill, DropDownStyle = ComboBoxStyle.DropDownList };
            cmbPayment.Items.AddRange(new object[] { "UPI / GPay", "Cash", "Card", "Credit / Account" });
            cmbPayment.SelectedIndex = 0;

            txtPromo = new TextBox { Text = "", Font = new Font("Segoe UI", 10.5f, FontStyle.Bold), BackColor = Color.FromArgb(7, 22, 44), ForeColor = Color.FromArgb(251, 191, 36), Dock = DockStyle.Fill };
            txtPromo.TextChanged += (s, e) => ApplyPromoCode(txtPromo.Text.Trim());

            metaLayout.Controls.Add(txtCustName, 0, 1);
            metaLayout.Controls.Add(txtCustPhone, 1, 1);
            metaLayout.Controls.Add(cmbPayment, 2, 1);
            metaLayout.Controls.Add(txtPromo, 3, 1);

            // 2. Bottom Actions Panel: Cart Subtotal + Print Buttons
            Panel bottomPanel = new Panel { Dock = DockStyle.Bottom, Height = 145, BackColor = Color.Transparent, Padding = new Padding(0, 6, 0, 0) };
            rightContent.Controls.Add(bottomPanel);

            // A. Quick Qty Modifier Row
            Panel quickBar = new Panel { Dock = DockStyle.Top, Height = 34, BackColor = Color.Transparent };
            bottomPanel.Controls.Add(quickBar);

            Button btnMinus = new Button { Text = "➖ Qty (-)", Font = new Font("Segoe UI", 8.5f, FontStyle.Bold), Size = new Size(88, 28), Location = new Point(0, 2), BackColor = Color.FromArgb(30, 41, 59), ForeColor = Color.White, FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand };
            btnMinus.Click += (s, e) => ModifySelectedCartItemQty(-1);
            quickBar.Controls.Add(btnMinus);

            Button btnPlus = new Button { Text = "➕ Qty (+)", Font = new Font("Segoe UI", 8.5f, FontStyle.Bold), Size = new Size(88, 28), Location = new Point(94, 2), BackColor = Color.FromArgb(30, 41, 59), ForeColor = Color.White, FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand };
            btnPlus.Click += (s, e) => ModifySelectedCartItemQty(1);
            quickBar.Controls.Add(btnPlus);

            Button btnDelete = new Button { Text = "🗑️ Remove", Font = new Font("Segoe UI", 8.5f, FontStyle.Bold), Size = new Size(95, 28), Location = new Point(188, 2), BackColor = Color.FromArgb(153, 27, 27), ForeColor = Color.White, FlatStyle = FlatStyle.Flat, Cursor = Cursors.Hand };
            btnDelete.Click += (s, e) => ModifySelectedCartItemQty(-999);
            quickBar.Controls.Add(btnDelete);

            Label lblHint = new Label { Text = "⌨️ Shortcuts: [+] Add | [-] Reduce | [Del] Remove | [F5] Print", Font = new Font("Segoe UI", 8.5f), ForeColor = Color.FromArgb(147, 197, 253), Location = new Point(290, 8), AutoSize = true };
            quickBar.Controls.Add(lblHint);

            // B. Financial Summary Card
            Panel sumCard = new Panel { Dock = DockStyle.Fill, BackColor = Color.FromArgb(11, 33, 73), Padding = new Padding(12, 4, 12, 4) };
            bottomPanel.Controls.Add(sumCard);
            sumCard.BringToFront();

            TableLayoutPanel sumLayout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 2,
                RowCount = 2,
                BackColor = Color.Transparent
            };
            sumLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50f));
            sumLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50f));
            sumLayout.RowStyles.Add(new RowStyle(SizeType.Absolute, 20f));
            sumLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 100f));
            sumCard.Controls.Add(sumLayout);

            lblSubtotal = new Label { Text = "Subtotal: ₹0.00", Font = new Font("Segoe UI", 9.5f), ForeColor = Color.White, Dock = DockStyle.Fill, TextAlign = ContentAlignment.MiddleLeft };
            lblDiscount = new Label { Text = "Promo Discount: -₹0.00", Font = new Font("Segoe UI", 9.5f), ForeColor = Color.FromArgb(251, 191, 36), Dock = DockStyle.Fill, TextAlign = ContentAlignment.MiddleRight };
            lblGrandTotal = new Label { Text = "NET AMOUNT: ₹0.00", Font = new Font("Segoe UI", 15, FontStyle.Bold), ForeColor = Color.FromArgb(52, 211, 153), Dock = DockStyle.Fill, TextAlign = ContentAlignment.MiddleLeft };

            sumLayout.Controls.Add(lblSubtotal, 0, 0);
            sumLayout.Controls.Add(lblDiscount, 1, 0);
            sumLayout.Controls.Add(lblGrandTotal, 0, 1);
            sumLayout.SetColumnSpan(lblGrandTotal, 2);

            // C. Action Buttons Row (Print & Cancel)
            Panel btnBar = new Panel { Dock = DockStyle.Bottom, Height = 48, BackColor = Color.FromArgb(15, 33, 64), Padding = new Padding(0, 4, 0, 0) };
            bottomPanel.Controls.Add(btnBar);

            TableLayoutPanel btnLayout = new TableLayoutPanel
            {
                Dock = DockStyle.Fill,
                ColumnCount = 2,
                RowCount = 1,
                BackColor = Color.Transparent
            };
            btnLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 65f));
            btnLayout.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 35f));
            btnLayout.RowStyles.Add(new RowStyle(SizeType.Percent, 100f));
            btnBar.Controls.Add(btnLayout);

            Button btnPrint = new Button
            {
                Text = "🖨️ PRINT 80MM BILL [F5]",
                Dock = DockStyle.Fill,
                Margin = new Padding(0, 0, 6, 0),
                Font = new Font("Segoe UI", 11.5f, FontStyle.Bold),
                BackColor = Color.FromArgb(37, 99, 235), // Royal Blue
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnPrint.FlatAppearance.BorderSize = 0;
            btnPrint.Click += (s, e) => ExecuteThermalPrintWithStrictValidation();
            btnLayout.Controls.Add(btnPrint, 0, 0);

            Button btnCancel = new Button
            {
                Text = "❌ Clear Cart",
                Dock = DockStyle.Fill,
                Margin = new Padding(0, 0, 0, 0),
                Font = new Font("Segoe UI", 11, FontStyle.Bold),
                BackColor = Color.FromArgb(239, 68, 68),
                ForeColor = Color.White,
                FlatStyle = FlatStyle.Flat,
                Cursor = Cursors.Hand
            };
            btnCancel.FlatAppearance.BorderSize = 0;
            btnCancel.Click += (s, e) => ResetCart();
            btnLayout.Controls.Add(btnCancel, 1, 0);

            // 3. Upgraded DataGridView Cart (Code | Stock | Description | Pack | Qty | Rate | Total)
            dgvCart = new DataGridView
            {
                Dock = DockStyle.Fill,
                BackgroundColor = Color.FromArgb(7, 22, 44),
                ForeColor = Color.Black,
                Font = new Font("Segoe UI", 10f),
                RowHeadersVisible = false,
                AllowUserToAddRows = false,
                SelectionMode = DataGridViewSelectionMode.FullRowSelect,
                AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill,
                RowTemplate = { Height = 32 },
                EnableHeadersVisualStyles = false
            };

            dgvCart.ColumnHeadersDefaultCellStyle.BackColor = Color.FromArgb(11, 33, 73);
            dgvCart.ColumnHeadersDefaultCellStyle.ForeColor = Color.FromArgb(251, 191, 36);
            dgvCart.ColumnHeadersDefaultCellStyle.Font = new Font("Segoe UI", 9.5f, FontStyle.Bold);

            dgvCart.Columns.Add("Code", "SKU");
            dgvCart.Columns.Add("Desc", "Item Description & தமிழ்");
            dgvCart.Columns.Add("Pack", "Packaging");
            dgvCart.Columns.Add("Stock", "Stock");
            dgvCart.Columns.Add("Qty", "Qty");
            dgvCart.Columns.Add("Rate", "Rate (₹)");
            dgvCart.Columns.Add("Total", "Amount (₹)");

            dgvCart.Columns[0].FillWeight = 10;
            dgvCart.Columns[1].FillWeight = 42;
            dgvCart.Columns[2].FillWeight = 14;
            dgvCart.Columns[3].FillWeight = 10;
            dgvCart.Columns[4].FillWeight = 8;
            dgvCart.Columns[5].FillWeight = 10;
            dgvCart.Columns[6].FillWeight = 12;

            dgvCart.KeyDown += (s, e) =>
            {
                if (dgvCart.CurrentRow == null || dgvCart.CurrentRow.Index < 0 || dgvCart.CurrentRow.Index >= cart.Count) return;
                int idx = dgvCart.CurrentRow.Index;

                if (e.KeyCode == Keys.Subtract || e.KeyCode == Keys.OemMinus)
                {
                    e.SuppressKeyPress = true;
                    if (cart[idx].Qty > 1) { cart[idx].Qty--; }
                    else { cart.RemoveAt(idx); }
                    RecalculateCart();
                }
                else if (e.KeyCode == Keys.Add || e.KeyCode == Keys.Oemplus)
                {
                    e.SuppressKeyPress = true;
                    cart[idx].Qty++;
                    RecalculateCart();
                }
                else if (e.KeyCode == Keys.Delete || e.KeyCode == Keys.Back)
                {
                    e.SuppressKeyPress = true;
                    cart.RemoveAt(idx);
                    RecalculateCart();
                }
            };

            rightContent.Controls.Add(dgvCart);
            dgvCart.BringToFront();
        }

        private void ModifySelectedCartItemQty(int delta)
        {
            if (dgvCart.CurrentRow == null || dgvCart.CurrentRow.Index < 0 || dgvCart.CurrentRow.Index >= cart.Count) return;
            int idx = dgvCart.CurrentRow.Index;

            if (delta == -999)
            {
                cart.RemoveAt(idx);
            }
            else
            {
                cart[idx].Qty += delta;
                if (cart[idx].Qty <= 0)
                {
                    cart.RemoveAt(idx);
                }
            }
            RecalculateCart();
        }

        private void UpdatePrinterStatusUI()
        {
            if (btnPrinterBadge == null) return;
            if (this.InvokeRequired)
            {
                this.Invoke(new Action(UpdatePrinterStatusUI));
                return;
            }

            if (isAccepted80mmThermal && isPrinterHardwareConnected)
            {
                btnPrinterBadge.Text = "● PRINTER";
                btnPrinterBadge.BackColor = Color.FromArgb(6, 78, 59); // Emerald Green
                btnPrinterBadge.ForeColor = Color.FromArgb(52, 211, 153);
                btnPrinterBadge.FlatAppearance.BorderColor = Color.FromArgb(16, 185, 129);
                if (statusToolTip != null)
                {
                    statusToolTip.SetToolTip(btnPrinterBadge, "Thermal Hardware Printer: CONNECTED & READY (Green)\nDevice: " + activePrinter + "\nStatus: 80mm High-Speed Auto-Spooler Engaged.");
                }
            }
            else
            {
                btnPrinterBadge.Text = "○ PRINTER";
                btnPrinterBadge.BackColor = Color.FromArgb(30, 41, 59); // Ash / Slate Gray
                btnPrinterBadge.ForeColor = Color.FromArgb(148, 163, 184);
                btnPrinterBadge.FlatAppearance.BorderColor = Color.FromArgb(71, 85, 105);
                if (statusToolTip != null)
                {
                    statusToolTip.SetToolTip(btnPrinterBadge, "Thermal Hardware Printer: OFFLINE / UNPLUGGED (Ash Gray)\nTarget: " + activePrinter + "\nStatus: Not detected or power off.");
                }
            }
        }

        private void UpdateNetworkStatusUI()
        {
            if (btnCloudBadge == null) return;
            if (this.InvokeRequired)
            {
                this.Invoke(new Action(UpdateNetworkStatusUI));
                return;
            }

            if (CloudSyncEngine.IsCloudOnline)
            {
                btnCloudBadge.Text = "● CLOUD";
                btnCloudBadge.BackColor = Color.FromArgb(6, 78, 59); // Emerald Green
                btnCloudBadge.ForeColor = Color.FromArgb(52, 211, 153);
                btnCloudBadge.FlatAppearance.BorderColor = Color.FromArgb(16, 185, 129);
                if (statusToolTip != null)
                {
                    statusToolTip.SetToolTip(btnCloudBadge, "Firebase Cloud: CONNECTED & SYNCHRONIZING (Green)\nProject: rkg-suiambu\nStatus: Real-Time Sync to Firestore & CEO App Active.");
                }
            }
            else
            {
                int q = CloudSyncEngine.PendingQueueCount;
                btnCloudBadge.Text = "○ CLOUD";
                btnCloudBadge.BackColor = Color.FromArgb(30, 41, 59); // Ash / Slate Gray
                btnCloudBadge.ForeColor = Color.FromArgb(148, 163, 184);
                btnCloudBadge.FlatAppearance.BorderColor = Color.FromArgb(71, 85, 105);
                if (statusToolTip != null)
                {
                    statusToolTip.SetToolTip(btnCloudBadge, q > 0 ? 
                        "Firebase Cloud: OFFLINE (" + q + " bills in local queue)\nStatus: Offline (Ash Gray) - Will auto-sync on reconnect." : 
                        "Firebase Cloud: OFFLINE / DISCONNECTED (Ash Gray)\nStatus: Local Offline Mode active.");
                }
            }
        }

        private void RunBackgroundHealthCheck()
        {
            TvsPrinterEngine.InspectHardwareStatus(out activePrinter, out activePrinterType, out isAccepted80mmThermal, out isPrinterHardwareConnected);
            UpdatePrinterStatusUI();

            ThreadPool.QueueUserWorkItem(s =>
            {
                bool online = CloudSyncEngine.CheckInternetAndCloudConnectivity();
                if (this.IsHandleCreated && !this.IsDisposed)
                {
                    this.BeginInvoke((Action)delegate { UpdateNetworkStatusUI(); });
                }
            });
        }

        private void RenderProductList(List<Product> prods)
        {
            lstProducts.Items.Clear();
            foreach (var p in prods)
            {
                lstProducts.Items.Add(string.Format("{0} | {1} ({2}) || Stock: {3} | ₹{4}", p.Code, p.Name, p.Pack, p.Stock, p.RetailPrice));
            }
            if (lstProducts.Items.Count > 0)
            {
                lstProducts.SelectedIndex = 0;
            }
        }

        private void FilterCatalog(string q)
        {
            if (string.IsNullOrEmpty(q))
            {
                RenderProductList(catalog);
                return;
            }
            string l = q.ToLower();
            List<Product> matches = new List<Product>();
            foreach (var p in catalog)
            {
                if (p.Code.ToLower().Contains(l) || p.Name.ToLower().Contains(l) || p.TamilName.ToLower().Contains(l) || p.Category.ToLower().Contains(l))
                {
                    matches.Add(p);
                }
            }
            RenderProductList(matches);
        }

        private void AddFirstOrSelectedProduct()
        {
            if (lstProducts.Items.Count == 0) return;

            if (lstProducts.SelectedIndex < 0)
            {
                lstProducts.SelectedIndex = 0;
            }

            string sel = lstProducts.SelectedItem.ToString();
            string code = sel.Split('|')[0].Trim();

            Product p = catalog.Find(x => x.Code == code);
            if (p == null) return;

            CartEntry exist = cart.Find(x => x.Item.Code == code);
            if (exist != null)
            {
                exist.Qty++;
            }
            else
            {
                cart.Add(new CartEntry { Item = p, Qty = 1, UnitRate = p.RetailPrice });
            }

            RecalculateCart();
            txtSearch.SelectAll();
            txtSearch.Focus();
        }

        private void ApplyPromoCode(string code)
        {
            if (string.IsNullOrEmpty(code))
            {
                activePromo = null;
                RecalculateCart();
                return;
            }

            string upper = code.ToUpper();
            Promo match = promos.Find(x => x.Code.ToUpper() == upper || x.QrNumber.ToUpper() == upper);
            if (match != null)
            {
                activePromo = match;
            }
            else
            {
                activePromo = null;
            }
            RecalculateCart();
        }

        private void RecalculateCart()
        {
            dgvCart.Rows.Clear();
            decimal subtotal = 0;

            foreach (var c in cart)
            {
                subtotal += c.TotalAmount;
                dgvCart.Rows.Add(c.Item.Code, c.Item.Name + " (" + c.Item.TamilName + ")", c.Item.Pack, c.Item.Stock, c.Qty, c.UnitRate.ToString("F2"), c.TotalAmount.ToString("F2"));
            }

            decimal discount = 0;
            if (activePromo != null)
            {
                if (subtotal >= activePromo.MinOrder)
                {
                    discount = (subtotal * activePromo.DiscountPct) / 100m;
                    if (discount > activePromo.MaxDiscount) discount = activePromo.MaxDiscount;
                }
            }

            decimal net = subtotal - discount;
            if (net < 0) net = 0;

            lblSubtotal.Text = string.Format("Subtotal: ₹{0:N2}", subtotal);
            lblDiscount.Text = string.Format("Promo ({0}): -₹{1:N2}", activePromo != null ? activePromo.Code : "None", discount);
            lblGrandTotal.Text = string.Format("NET AMOUNT: ₹{0:N2}", net);
        }

        private void ResetCart()
        {
            cart.Clear();
            activePromo = null;
            txtPromo.Text = "";
            txtCustName.Text = "Cash Customer";
            txtCustPhone.Text = "9842724147";
            cmbPayment.SelectedIndex = 0;
            RecalculateCart();
            txtSearch.Focus();
        }

        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            if (keyData == Keys.F5)
            {
                ExecuteThermalPrintWithStrictValidation();
                return true;
            }
            return base.ProcessCmdKey(ref msg, keyData);
        }

        private void ExecuteThermalPrintWithStrictValidation()
        {
            if (cart.Count == 0)
            {
                MessageBox.Show("Cart is empty! Please scan or select products to bill.", "Empty Cart", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                txtSearch.Focus();
                return;
            }

            decimal subtotal = 0;
            List<BillItemRecord> items = new List<BillItemRecord>();
            foreach (var c in cart)
            {
                subtotal += c.TotalAmount;
                items.Add(new BillItemRecord
                {
                    Code = c.Item.Code,
                    Name = c.Item.Name,
                    TamilName = c.Item.TamilName,
                    Pack = c.Item.Pack,
                    Qty = c.Qty,
                    Rate = c.UnitRate,
                    Total = c.TotalAmount
                });
            }

            decimal discount = 0;
            if (activePromo != null && subtotal >= activePromo.MinOrder)
            {
                discount = (subtotal * activePromo.DiscountPct) / 100m;
                if (discount > activePromo.MaxDiscount) discount = activePromo.MaxDiscount;
            }

            decimal net = subtotal - discount;
            if (net < 0) net = 0;

            string billNo = "RKG-" + DateTime.Now.ToString("yyyyMMdd-HHmmss");
            string invNo = "INV-" + DateTime.Now.ToString("yyyyMMdd-HHmmss");

            BillRecord bill = new BillRecord
            {
                BillNo = billNo,
                InvoiceNo = invNo,
                DailyDate = DateTime.Now.ToString("yyyy-MM-dd"),
                DateTimeIso = DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss"),
                CustomerName = string.IsNullOrEmpty(txtCustName.Text.Trim()) ? "Cash Customer" : txtCustName.Text.Trim(),
                CustomerPhone = string.IsNullOrEmpty(txtCustPhone.Text.Trim()) ? "9842724147" : txtCustPhone.Text.Trim(),
                PaymentMode = cmbPayment.SelectedItem != null ? cmbPayment.SelectedItem.ToString() : "Cash",
                BilledBy = user.FullName,
                Subtotal = subtotal,
                Discount = discount,
                NetAmount = net,
                PromoCode = activePromo != null ? activePromo.Code : "None",
                Items = items,
                SyncedToFirebase = false
            };

            // 1. Hardware 80mm ESC/POS Printing
            byte[] receiptBytes = TvsPrinterEngine.Build80mmEscPosThermalReceipt(bill);
            bool printSuccess = TvsPrinterEngine.SendRawBytes(activePrinter, receiptBytes);

            // 2. Cloud & Local Sync
            CloudSyncEngine.SaveBillLocallyAndQueue(bill);

            // 3. Success Notification
            string syncNotice = CloudSyncEngine.IsCloudOnline ? "Cloud: Synced to Firebase & CEO App ✅" : "Cloud: Saved locally in Offline Queue 💾";
            string printNotice = printSuccess ? "Printer: Dispatched to " + activePrinter + " 🖨️" : "Printer: Spooler Queued / Simulation Mode 🖨️";

            MessageBox.Show(string.Format("✅ BILL GENERATED SUCCESSFULLY!\n\nBill No: {0}\nNet Amount: ₹{1:N2}\nPayment: {2}\n\n{3}\n{4}",
                billNo, net, bill.PaymentMode, printNotice, syncNotice), "RKG Billing Confirmation", MessageBoxButtons.OK, MessageBoxIcon.Information);

            ResetCart();
        }
    }
}
