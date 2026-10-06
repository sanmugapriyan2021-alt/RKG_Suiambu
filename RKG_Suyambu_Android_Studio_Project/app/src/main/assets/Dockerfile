# Nginx Alpine Container for RKG Suyambu Public Frontend & CEO Portal
FROM nginx:1.25-alpine

# Remove default nginx static files
RUN rm -rf /usr/share/nginx/html/*

# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy all website assets, pages, scripts, styles, and image directories
COPY . /usr/share/nginx/html/

# Clean up any non-frontend files from nginx webroot
RUN rm -rf /usr/share/nginx/html/Dockerfile \
           /usr/share/nginx/html/nginx.conf \
           /usr/share/nginx/html/.dockerignore \
           /usr/share/nginx/html/RKG_Suyambu_Web_ERP_Backend \
           /usr/share/nginx/html/.git

# Expose HTTP port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1

# Launch Nginx in foreground
CMD ["nginx", "-g", "daemon off;"]
