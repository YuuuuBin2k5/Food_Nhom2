<%@ page contentType="text/html;charset=UTF-8" %>
<!-- Đăng ký Service Worker cho PWA -->
<script>
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', () => {
            navigator.serviceWorker.register('${pageContext.request.contextPath}/sw.js')
                .then((reg) => console.log('Service Worker registered'))
                .catch((err) => console.log('Service Worker registration failed:', err));
        });
    }
</script>
