const CACHE_NAME = 'freshsave-shipper-v1';
const urlsToCache = [
  // Không cache trang chính vì cần context path động
  // Chỉ cache các static files
];

// Install - skip waiting để activate ngay
self.addEventListener('install', (event) => {
  console.log('Service Worker installing...');
  self.skipWaiting();
});

// Fetch - Network first, fallback to cache
self.addEventListener('fetch', (event) => {
  event.respondWith(
    fetch(event.request)
      .then((response) => {
        // Cache successful responses
        if (response && response.status === 200) {
          const responseToCache = response.clone();
          caches.open(CACHE_NAME).then((cache) => {
            // Chỉ cache GET requests và static files
            if (event.request.method === 'GET' && 
                (event.request.url.includes('/css/') || 
                 event.request.url.includes('/js/') || 
                 event.request.url.includes('/images/'))) {
              cache.put(event.request, responseToCache);
            }
          });
        }
        return response;
      })
      .catch(() => {
        // Offline - try cache
        return caches.match(event.request);
      })
  );
});

// Activate - claim clients và xóa cache cũ
self.addEventListener('activate', (event) => {
  console.log('Service Worker activated');
  event.waitUntil(
    Promise.all([
      self.clients.claim(),
      caches.keys().then((cacheNames) => {
        return Promise.all(
          cacheNames.map((cacheName) => {
            if (cacheName !== CACHE_NAME) {
              console.log('Deleting old cache:', cacheName);
              return caches.delete(cacheName);
            }
          })
        );
      })
    ])
  );
});
