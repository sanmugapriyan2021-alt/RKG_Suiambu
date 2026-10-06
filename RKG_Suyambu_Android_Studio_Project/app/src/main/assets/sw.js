const CACHE_NAME = 'rkg-ceo-cache-v1';
const STATIC_ASSETS = [
  '/mobile',
  'mobile.js',
  'manifest.json',
  'rkg-logo-gold.jpg',
  'https://cdn.tailwindcss.com',
  'https://unpkg.com/lucide@latest',
  'https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;700;800;900&family=Mukta+Malar:wght@400;600;700;800&family=JetBrains+Mono:wght@500;700&display=swap'
];

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => {
      return cache.addAll(STATIC_ASSETS).catch(err => console.log('SW cache addAll error:', err));
    })
  );
  self.skipWaiting();
});

self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => Promise.all(
      keys.map(k => {
        if (k !== CACHE_NAME) return caches.delete(k);
      })
    ))
  );
  self.clients.claim();
});

self.addEventListener('fetch', event => {
  if (event.request.method !== 'GET') return;
  if (event.request.url.includes('/api/')) return; // Network-only for live API data

  event.respondWith(
    caches.match(event.request).then(cachedResp => {
      const fetchPromise = fetch(event.request).then(networkResp => {
        if (networkResp && networkResp.status === 200) {
          const respClone = networkResp.clone();
          caches.open(CACHE_NAME).then(cache => cache.put(event.request, respClone));
        }
        return networkResp;
      }).catch(() => cachedResp);

      return cachedResp || fetchPromise;
    })
  );
});

