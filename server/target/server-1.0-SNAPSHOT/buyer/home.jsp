<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang chủ - FreshSave</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        /* Tailwind-like utilities */
        .bg-white { background-color: #ffffff; }
        .bg-gray-50 { background-color: #f9fafb; }
        .bg-gray-900 { background-color: #111827; }
        .text-white { color: #ffffff; }
        .text-gray-400 { color: #9ca3af; }
        .text-gray-600 { color: #4b5563; }
        .text-gray-700 { color: #374151; }
        .text-gray-900 { color: #111827; }
        .text-orange-600 { color: #ea580c; }
        .text-orange-700 { color: #c2410c; }
        .text-orange-800 { color: #9a3412; }
        .text-amber-600 { color: #d97706; }
        .text-amber-700 { color: #b45309; }
        .text-red-600 { color: #dc2626; }
        .text-red-700 { color: #b91c1c; }
        
        .bg-gradient-to-br { background-image: linear-gradient(to bottom right, var(--tw-gradient-stops)); }
        .from-orange-50 { --tw-gradient-from: #fff7ed; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(255, 247, 237, 0)); }
        .via-amber-50 { --tw-gradient-stops: var(--tw-gradient-from), #fffbeb, var(--tw-gradient-to, rgba(255, 251, 235, 0)); }
        .to-yellow-50 { --tw-gradient-to: #fefce8; }
        
        .relative { position: relative; }
        .absolute { position: absolute; }
        .inset-0 { top: 0; right: 0; bottom: 0; left: 0; }
        .z-10 { z-index: 10; }
        .-z-10 { z-index: -10; }
        
        .flex { display: flex; }
        .inline-flex { display: inline-flex; }
        .grid { display: grid; }
        .hidden { display: none; }
        
        .items-center { align-items: center; }
        .items-start { align-items: flex-start; }
        .justify-center { justify-content: center; }
        .justify-between { justify-content: space-between; }
        
        .gap-1 { gap: 0.25rem; }
        .gap-1\.5 { gap: 0.375rem; }
        .gap-2 { gap: 0.5rem; }
        .gap-3 { gap: 0.75rem; }
        .gap-4 { gap: 1rem; }
        .gap-5 { gap: 1.25rem; }
        .gap-6 { gap: 1.5rem; }
        .gap-8 { gap: 2rem; }
        
        .space-y-2 > * + * { margin-top: 0.5rem; }
        .space-y-2\.5 > * + * { margin-top: 0.625rem; }
        .space-y-4 > * + * { margin-top: 1rem; }
        
        .overflow-hidden { overflow: hidden; }
        .rounded-lg { border-radius: 0.5rem; }
        .rounded-xl { border-radius: 0.75rem; }
        .rounded-full { border-radius: 9999px; }
        
        .border-l-4 { border-left-width: 4px; }
        .border-2 { border-width: 2px; }
        .border-orange-500 { border-color: #f97316; }
        .border-orange-600 { border-color: #ea580c; }
        .border-amber-500 { border-color: #f59e0b; }
        .border-red-500 { border-color: #ef4444; }
        .border-red-600 { border-color: #dc2626; }
        .border-gray-700 { border-color: #374151; }
        .border-gray-800 { border-color: #1f2937; }
        
        .shadow-sm { box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05); }
        .shadow-xl { box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1); }
        .shadow-2xl { box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25); }
        
        .px-2 { padding-left: 0.5rem; padding-right: 0.5rem; }
        .px-2\.5 { padding-left: 0.625rem; padding-right: 0.625rem; }
        .px-3 { padding-left: 0.75rem; padding-right: 0.75rem; }
        .px-4 { padding-left: 1rem; padding-right: 1rem; }
        .px-5 { padding-left: 1.25rem; padding-right: 1.25rem; }
        .px-6 { padding-left: 1.5rem; padding-right: 1.5rem; }
        
        .py-1 { padding-top: 0.25rem; padding-bottom: 0.25rem; }
        .py-2 { padding-top: 0.5rem; padding-bottom: 0.5rem; }
        .py-3 { padding-top: 0.75rem; padding-bottom: 0.75rem; }
        .py-4 { padding-top: 1rem; padding-bottom: 1rem; }
        .py-6 { padding-top: 1.5rem; padding-bottom: 1.5rem; }
        .py-8 { padding-top: 2rem; padding-bottom: 2rem; }
        .py-10 { padding-top: 2.5rem; padding-bottom: 2.5rem; }
        .py-14 { padding-top: 3.5rem; padding-bottom: 3.5rem; }
        .py-32 { padding-top: 8rem; padding-bottom: 8rem; }
        
        .p-2\.5 { padding: 0.625rem; }
        .p-4 { padding: 1rem; }
        .p-6 { padding: 1.5rem; }
        
        .mb-1 { margin-bottom: 0.25rem; }
        .mb-2 { margin-bottom: 0.5rem; }
        .mb-3 { margin-bottom: 0.75rem; }
        .mb-4 { margin-bottom: 1rem; }
        .mb-6 { margin-bottom: 1.5rem; }
        .mb-8 { margin-bottom: 2rem; }
        .mt-0\.5 { margin-top: 0.125rem; }
        .mt-1 { margin-top: 0.25rem; }
        .mt-0\.5 { margin-top: 0.125rem; }
        .ml-2 { margin-left: 0.5rem; }
        
        .w-10 { width: 2.5rem; }
        .w-14 { width: 3.5rem; }
        .w-20 { width: 5rem; }
        .w-64 { width: 16rem; }
        .w-96 { width: 24rem; }
        .w-1\/2 { width: 50%; }
        .w-full { width: 100%; }
        
        .h-10 { height: 2.5rem; }
        .h-14 { height: 3.5rem; }
        .h-20 { height: 5rem; }
        .h-64 { height: 16rem; }
        .h-96 { height: 24rem; }
        .h-0\.5 { height: 0.125rem; }
        .h-1\/3 { height: 33.333333%; }
        .h-full { height: 100%; }
        
        .min-h-\[65vh\] { min-height: 65vh; }
        
        .max-w-7xl { max-width: 80rem; }
        .max-w-xl { max-width: 36rem; }
        
        .text-xs { font-size: 0.75rem; line-height: 1rem; }
        .text-sm { font-size: 0.875rem; line-height: 1.25rem; }
        .text-lg { font-size: 1.125rem; line-height: 1.75rem; }
        .text-xl { font-size: 1.25rem; line-height: 1.75rem; }
        .text-2xl { font-size: 1.5rem; line-height: 2rem; }
        .text-\[8px\] { font-size: 8px; }
        .text-\[10px\] { font-size: 10px; }
        
        .font-bold { font-weight: 700; }
        .font-black { font-weight: 900; }
        
        .uppercase { text-transform: uppercase; }
        .tracking-tight { letter-spacing: -0.025em; }
        .tracking-wider { letter-spacing: 0.05em; }
        .leading-tight { line-height: 1.25; }
        .leading-relaxed { line-height: 1.625; }
        .leading-\[0\.95\] { line-height: 0.95; }
        
        .line-clamp-2 {
            overflow: hidden;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
        }
        
        .line-through { text-decoration-line: line-through; }
        
        .cursor-pointer { cursor: pointer; }
        .select-none { user-select: none; }
        
        .transition-all { transition-property: all; transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1); transition-duration: 150ms; }
        .duration-300 { transition-duration: 300ms; }
        .duration-500 { transition-duration: 500ms; }
        
        .hover\:shadow-lg:hover { box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.1); }
        .hover\:shadow-xl:hover { box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1); }
        .hover\:border-emerald-500:hover { border-color: #10b981; }
        
        .group:hover .group-hover\:translate-x-1 { transform: translateX(0.25rem); }
        
        .backdrop-blur-sm { backdrop-filter: blur(4px); }
        .blur-2xl { filter: blur(40px); }
        .blur-3xl { filter: blur(64px); }
        
        .drop-shadow-sm { filter: drop-shadow(0 1px 1px rgb(0 0 0 / 0.05)); }
        
        .aspect-\[7\/5\] { aspect-ratio: 7 / 5; }
        .aspect-\[3\/4\] { aspect-ratio: 3 / 4; }
        
        .object-cover { object-fit: cover; }
        
        .bg-clip-text { -webkit-background-clip: text; background-clip: text; }
        .text-transparent { color: transparent; }
        
        /* Grid */
        .grid-cols-2 { grid-template-columns: repeat(2, minmax(0, 1fr)); }
        .grid-cols-3 { grid-template-columns: repeat(3, minmax(0, 1fr)); }
        
        /* Responsive */
        @media (min-width: 640px) {
            .sm\:px-6 { padding-left: 1.5rem; padding-right: 1.5rem; }
            .sm\:flex-row { flex-direction: row; }
        }
        
        @media (min-width: 768px) {
            .md\:grid-cols-3 { grid-template-columns: repeat(3, minmax(0, 1fr)); }
            .md\:text-2xl { font-size: 1.5rem; line-height: 2rem; }
            .md\:text-3xl { font-size: 1.875rem; line-height: 2.25rem; }
        }
        
        @media (min-width: 1024px) {
            .lg\:px-7 { padding-left: 1.75rem; padding-right: 1.75rem; }
            .lg\:px-8 { padding-left: 2rem; padding-right: 2rem; }
            .lg\:grid-cols-4 { grid-template-columns: repeat(4, minmax(0, 1fr)); }
            .lg\:grid-cols-12 { grid-template-columns: repeat(12, minmax(0, 1fr)); }
            .lg\:col-span-5 { grid-column: span 5 / span 5; }
            .lg\:col-span-7 { grid-column: span 7 / span 7; }
            .lg\:flex-row { flex-direction: row; }
            .lg\:items-end { align-items: flex-end; }
            .lg\:justify-between { justify-content: space-between; }
            .lg\:gap-8 { gap: 2rem; }
            .lg\:text-4xl { font-size: 2.25rem; line-height: 2.5rem; }
        }
        
        /* Custom gradients */
        .bg-gradient-to-r { background-image: linear-gradient(to right, var(--tw-gradient-stops)); }
        .from-orange-500\/15 { --tw-gradient-from: rgba(249, 115, 22, 0.15); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(249, 115, 22, 0)); }
        .via-amber-500\/15 { --tw-gradient-stops: var(--tw-gradient-from), rgba(245, 158, 11, 0.15), var(--tw-gradient-to, rgba(245, 158, 11, 0)); }
        .to-yellow-500\/15 { --tw-gradient-to: rgba(234, 179, 8, 0.15); }
        
        .from-orange-600 { --tw-gradient-from: #ea580c; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(234, 88, 12, 0)); }
        .via-amber-600 { --tw-gradient-stops: var(--tw-gradient-from), #d97706, var(--tw-gradient-to, rgba(217, 119, 6, 0)); }
        .to-yellow-600 { --tw-gradient-to: #ca8a04; }
        
        .from-red-600 { --tw-gradient-from: #dc2626; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(220, 38, 38, 0)); }
        .via-orange-600 { --tw-gradient-stops: var(--tw-gradient-from), #ea580c, var(--tw-gradient-to, rgba(234, 88, 12, 0)); }
        .to-amber-600 { --tw-gradient-to: #d97706; }
        
        .from-orange-500 { --tw-gradient-from: #f97316; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(249, 115, 22, 0)); }
        .via-amber-500 { --tw-gradient-stops: var(--tw-gradient-from), #f59e0b, var(--tw-gradient-to, rgba(245, 158, 11, 0)); }
        .to-yellow-500 { --tw-gradient-to: #eab308; }
        
        .from-amber-400 { --tw-gradient-from: #fbbf24; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(251, 191, 36, 0)); }
        .to-orange-500 { --tw-gradient-to: #f97316; }
        
        .from-emerald-400 { --tw-gradient-from: #34d399; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(52, 211, 153, 0)); }
        .to-teal-500 { --tw-gradient-to: #14b8a6; }
        
        .from-blue-400 { --tw-gradient-from: #60a5fa; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(96, 165, 250, 0)); }
        .to-indigo-500 { --tw-gradient-to: #6366f1; }
        
        .from-pink-400 { --tw-gradient-from: #f472b6; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(244, 114, 182, 0)); }
        .to-rose-500 { --tw-gradient-to: #f43f5e; }
        
        .from-orange-100\/80 { --tw-gradient-from: rgba(255, 237, 213, 0.8); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(255, 237, 213, 0)); }
        .to-amber-100\/80 { --tw-gradient-to: rgba(254, 243, 199, 0.8); }
        
        .from-amber-100\/80 { --tw-gradient-from: rgba(254, 243, 199, 0.8); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(254, 243, 199, 0)); }
        .to-yellow-100\/80 { --tw-gradient-to: rgba(254, 249, 195, 0.8); }
        
        .from-red-100\/80 { --tw-gradient-from: rgba(254, 226, 226, 0.8); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(254, 226, 226, 0)); }
        .to-pink-100\/80 { --tw-gradient-to: rgba(252, 231, 243, 0.8); }
        
        .from-orange-900\/80 { --tw-gradient-from: rgba(124, 45, 18, 0.8); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(124, 45, 18, 0)); }
        
        .from-orange-50\/30 { --tw-gradient-from: rgba(255, 247, 237, 0.3); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(255, 247, 237, 0)); }
        
        .from-emerald-500 { --tw-gradient-from: #10b981; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(16, 185, 129, 0)); }
        .to-teal-600 { --tw-gradient-to: #0d9488; }
        
        .from-orange-400 { --tw-gradient-from: #fb923c; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(251, 146, 60, 0)); }
        .to-amber-400 { --tw-gradient-to: #fbbf24; }
        
        .bg-gray-800\/50 { background-color: rgba(31, 41, 55, 0.5); }
        .bg-orange-500\/20 { background-color: rgba(249, 115, 22, 0.2); }
        .text-gray-700\/30 { color: rgba(55, 65, 81, 0.3); }
        
        .bg-gradient-to-t { background-image: linear-gradient(to top, var(--tw-gradient-stops)); }
        .bg-gradient-to-b { background-image: linear-gradient(to bottom, var(--tw-gradient-stops)); }
        .bg-gradient-to-bl { background-image: linear-gradient(to bottom left, var(--tw-gradient-stops)); }
        .bg-gradient-to-tr { background-image: linear-gradient(to top right, var(--tw-gradient-stops)); }
        
        .from-orange-100\/60 { --tw-gradient-from: rgba(255, 237, 213, 0.6); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(255, 237, 213, 0)); }
        .via-amber-100\/40 { --tw-gradient-stops: var(--tw-gradient-from), rgba(254, 243, 199, 0.4), var(--tw-gradient-to, rgba(254, 243, 199, 0)); }
        
        .from-orange-200\/40 { --tw-gradient-from: rgba(254, 215, 170, 0.4); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(254, 215, 170, 0)); }
        .via-amber-200\/30 { --tw-gradient-stops: var(--tw-gradient-from), rgba(253, 230, 138, 0.3), var(--tw-gradient-to, rgba(253, 230, 138, 0)); }
        
        .from-yellow-200\/30 { --tw-gradient-from: rgba(254, 240, 138, 0.3); --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(254, 240, 138, 0)); }
        
        .from-amber-400 { --tw-gradient-from: #fbbf24; --tw-gradient-stops: var(--tw-gradient-from), var(--tw-gradient-to, rgba(251, 191, 36, 0)); }
        .to-orange-600 { --tw-gradient-to: #ea580c; }
        
        .bg-red-100 { background-color: #fee2e2; }
        .bg-orange-100 { background-color: #ffedd5; }
        
        .text-emerald-400 { color: #34d399; }
        
        .-bottom-3 { bottom: -0.75rem; }
        .-right-3 { right: -0.75rem; }
        .-top-4 { top: -1rem; }
        .-right-2 { right: -0.5rem; }
        
        .top-0 { top: 0; }
        .right-0 { right: 0; }
        .bottom-0 { bottom: 0; }
        .left-0 { left: 0; }
        .top-2 { top: 0.5rem; }
        .left-2 { left: 0.5rem; }
        .top-1\/4 { top: 25%; }
        .right-1\/4 { right: 25%; }
        
        .border-t { border-top-width: 1px; }
        
        .text-center { text-align: center; }
        
        .mx-auto { margin-left: auto; margin-right: auto; }
        
        .block { display: block; }
        .inline-block { display: inline-block; }
        
        .flex-col { flex-direction: column; }
    </style>

</head>
<body class="bg-white">
    <!-- Include Sidebar -->
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="currentPath" value="/" />
    </jsp:include>
    
    <!-- Hero Section -->
    <section class="relative min-h-[65vh] flex items-center justify-center overflow-hidden bg-gradient-to-br from-orange-50 via-amber-50 to-yellow-50" style="margin-top: 96px;">
        <!-- Geometric Background -->
        <div class="absolute inset-0">
            <div class="absolute top-0 right-0 w-1/2 h-full bg-gradient-to-bl from-orange-100/60 via-amber-100/40 to-transparent"></div>
            <div class="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-orange-200/40 via-amber-200/30 to-transparent blur-3xl"></div>
            <div class="absolute top-1/4 right-1/4 w-64 h-64 bg-gradient-to-br from-yellow-200/30 to-transparent blur-2xl rounded-full"></div>
        </div>
        
        <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 grid lg:grid-cols-12 gap-5 items-center">
            <!-- Left Content -->
            <div class="lg:col-span-7 space-y-4">
                <!-- Badge -->
                <div class="inline-flex items-center gap-1.5 px-2.5 py-1 bg-gradient-to-r from-orange-500/15 via-amber-500/15 to-yellow-500/15 border-l-4 border-orange-500 backdrop-blur-sm shadow-sm">
                    <span style="font-size: 0.75rem;">✨</span>
                    <span class="text-[10px] font-bold text-orange-800">
                        Miễn phí giao hàng • Đơn từ 200k
                    </span>
                </div>
                
                <!-- Heading -->
                <div class="space-y-2">
                    <h1 class="text-2xl md:text-3xl lg:text-4xl font-black leading-[0.95] tracking-tight drop-shadow-sm">
                        <span class="block text-gray-900">THỰC PHẨM</span>
                        <span class="block text-gray-900">TƯƠI NGON</span>
                        <span class="block mt-1 bg-gradient-to-r from-orange-600 via-amber-600 to-yellow-600 bg-clip-text text-transparent">
                            GIÁ SIÊU RẺ
                        </span>
                    </h1>
                    
                    <div class="flex items-center gap-2 pt-1">
                        <div class="h-0.5 w-10 bg-gradient-to-r from-orange-500 via-amber-500 to-yellow-500 shadow-sm"></div>
                        <p class="text-xs text-gray-700 max-w-xl">
                            Mua sắm thông minh với sản phẩm chất lượng sắp hết hạn. 
                            <span class="font-black text-orange-600"> Giảm tới 70%</span>
                        </p>
                    </div>
                </div>

                <!-- CTAs -->
                <div class="flex flex-col sm:flex-row gap-2">
                    <a href="${pageContext.request.contextPath}/products" 
                       class="group relative px-5 py-2 bg-gradient-to-r from-orange-500 via-amber-500 to-yellow-500 text-white overflow-hidden hover:shadow-lg transition-all duration-300 font-bold rounded-lg text-center">
                        <span class="relative flex items-center justify-center gap-1.5 text-xs drop-shadow-sm">
                            MUA NGAY
                            <span class="group-hover:translate-x-1 transition-transform">→</span>
                        </span>
                    </a>
                    
                    <a href="${pageContext.request.contextPath}/products" 
                       class="px-5 py-2 border-2 border-orange-600 text-orange-700 font-bold text-xs hover:bg-orange-600 hover:text-white transition-all duration-300 rounded-lg text-center">
                        XEM DEAL HOT
                    </a>
                </div>

                <!-- Stats -->
                <div class="grid grid-cols-3 gap-2 pt-2">
                    <div class="relative p-2.5 bg-gradient-to-br from-orange-100/80 to-amber-100/80 border-l-4 border-orange-500 overflow-hidden group hover:shadow-md transition-all backdrop-blur-sm">
                        <div class="relative">
                            <div class="text-lg font-black text-orange-700 drop-shadow-sm">
                                ${productsCount >= 1000 ? (productsCount / 1000) : productsCount}${productsCount >= 1000 ? 'K+' : '+'}
                            </div>
                            <div class="text-[8px] font-bold text-orange-600 uppercase tracking-wider mt-0.5">Sản phẩm</div>
                        </div>
                    </div>
                    
                    <div class="relative p-2.5 bg-gradient-to-br from-amber-100/80 to-yellow-100/80 border-l-4 border-amber-500 overflow-hidden group hover:shadow-md transition-all backdrop-blur-sm">
                        <div class="relative">
                            <div class="text-lg font-black text-amber-700 drop-shadow-sm">2H</div>
                            <div class="text-[8px] font-bold text-amber-600 uppercase tracking-wider mt-0.5">Giao hàng</div>
                        </div>
                    </div>
                    
                    <div class="relative p-2.5 bg-gradient-to-br from-red-100/80 to-pink-100/80 border-l-4 border-red-500 overflow-hidden group hover:shadow-md transition-all backdrop-blur-sm">
                        <div class="relative flex items-start gap-1">
                            <span class="text-red-600 mt-0.5 drop-shadow-sm">📉</span>
                            <div>
                                <div class="text-lg font-black text-red-700 drop-shadow-sm">70%</div>
                                <div class="text-[8px] font-bold text-red-600 uppercase tracking-wider mt-0.5">Giảm giá</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Right Image -->
            <div class="lg:col-span-5 relative">
                <div class="relative aspect-[3/4] overflow-hidden shadow-2xl rounded-lg">
                    <c:choose>
                        <c:when test="${not empty products and products.size() > 0}">
                            <img src="${products[0].imageUrl != null ? products[0].imageUrl : 'https://images.unsplash.com/photo-1542838132-92c53300491e?w=1080'}" 
                                 alt="Featured product" class="w-full h-full object-cover">
                        </c:when>
                        <c:otherwise>
                            <img src="https://images.unsplash.com/photo-1542838132-92c53300491e?w=1080" 
                                 alt="Featured product" class="w-full h-full object-cover">
                        </c:otherwise>
                    </c:choose>
                    
                    <div class="absolute bottom-0 left-0 right-0 h-1/3 bg-gradient-to-t from-orange-900/80 to-transparent"></div>
                </div>
                <div class="absolute -bottom-3 -right-3 w-20 h-20 bg-gradient-to-br from-amber-400 to-orange-600 -z-10 rounded-lg"></div>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="py-8 bg-gray-50 relative overflow-hidden">
        <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="text-center mb-8">
                <div class="inline-block px-6 py-3 bg-gradient-to-r from-orange-500/10 via-amber-500/10 to-yellow-500/10 border-l-4 border-orange-500 shadow-sm">
                    <span class="text-sm font-bold text-orange-800 uppercase tracking-wider">
                        Tại sao chọn chúng tôi
                    </span>
                </div>
            </div>

            <div class="grid grid-cols-2 lg:grid-cols-4 gap-8">
                <div class="flex flex-col items-center text-center">
                    <div class="w-10 h-10 bg-gradient-to-br from-amber-400 to-orange-500 flex items-center justify-center mb-3">
                        <span class="text-white text-xl">⏰</span>
                    </div>
                    <h3 class="text-sm font-bold text-gray-900 mb-1">Tươi ngon đảm bảo</h3>
                    <p class="text-xs text-gray-600 leading-relaxed">Sản phẩm gần hết hạn nhưng vẫn giữ được chất lượng tuyệt đối</p>
                </div>

                <div class="flex flex-col items-center text-center">
                    <div class="w-10 h-10 bg-gradient-to-br from-emerald-400 to-teal-500 flex items-center justify-center mb-3">
                        <span class="text-white text-xl">📉</span>
                    </div>
                    <h3 class="text-sm font-bold text-gray-900 mb-1">Giá siêu rẻ</h3>
                    <p class="text-xs text-gray-600 leading-relaxed">Tiết kiệm tới 70% so với giá thông thường</p>
                </div>

                <div class="flex flex-col items-center text-center">
                    <div class="w-10 h-10 bg-gradient-to-br from-blue-400 to-indigo-500 flex items-center justify-center mb-3">
                        <span class="text-white text-xl">🚚</span>
                    </div>
                    <h3 class="text-sm font-bold text-gray-900 mb-1">Giao hàng 2H</h3>
                    <p class="text-xs text-gray-600 leading-relaxed">Miễn phí ship cho đơn từ 200.000đ</p>
                </div>

                <div class="flex flex-col items-center text-center">
                    <div class="w-10 h-10 bg-gradient-to-br from-pink-400 to-rose-500 flex items-center justify-center mb-3">
                        <span class="text-white text-xl">❤️</span>
                    </div>
                    <h3 class="text-sm font-bold text-gray-900 mb-1">Bảo vệ môi trường</h3>
                    <p class="text-xs text-gray-600 leading-relaxed">Giảm thiểu lãng phí thực phẩm hiệu quả</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Products Section -->
    <section class="py-14 bg-gradient-to-b from-orange-50/30 to-white">
        <div class="max-w-7xl mx-auto px-3 sm:px-5 lg:px-7">
            <div class="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-3 mb-6">
                <div class="space-y-2\.5">
                    <div class="inline-block px-2.5 py-1 bg-red-100 border-l-4 border-red-600">
                        <span class="text-[10px] font-bold text-red-800 uppercase tracking-wider">Deal hot</span>
                    </div>
                    <h2 class="text-xl md:text-2xl font-black text-gray-900 leading-tight">
                        SẢN PHẨM<br>
                        <span class="bg-gradient-to-r from-red-600 via-orange-600 to-amber-600 bg-clip-text text-transparent">
                            NỔI BẬT
                        </span>
                    </h2>
                </div>
                
                <a href="${pageContext.request.contextPath}/products" 
                   class="px-4 py-2 bg-gradient-to-r from-orange-600 to-amber-600 text-white font-bold text-xs hover:shadow-lg transition-all duration-300 rounded-lg">
                    XEM TẤT CẢ →
                </a>
            </div>

            <c:choose>
                <c:when test="${not empty products}">
                    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                        <c:forEach items="${products}" var="product" begin="0" end="11">
                            <div class="bg-white rounded-xl overflow-hidden shadow-sm hover:shadow-xl transition-all cursor-pointer" 
                                 onclick="location.href='${pageContext.request.contextPath}/products/${product.productId}'">
                                <div class="relative">
                                    <c:set var="discount" value="${((product.originalPrice - product.salePrice) / product.originalPrice * 100)}" />
                                    <c:if test="${product.originalPrice > product.salePrice}">
                                        <div class="absolute top-2 left-2 bg-white text-red-600 px-2 py-1 rounded-full font-bold text-xs shadow">
                                            -<fmt:formatNumber value="${discount}" maxFractionDigits="0"/>%
                                        </div>
                                    </c:if>
                                    <img src="${product.imageUrl != null ? product.imageUrl : 'https://placehold.co/400x300/FF6B6B/FFFFFF?text=Food'}" 
                                         alt="${product.name}" class="w-full aspect-[7/5] object-cover">
                                </div>
                                <div class="p-4">
                                    <h3 class="font-bold text-gray-900 mb-2 line-clamp-2">${product.name}</h3>
                                    <p class="text-sm text-gray-600 mb-3">🏪 ${product.seller.shopName}</p>
                                    <div class="flex justify-between items-center">
                                        <div>
                                            <span class="text-xl font-bold text-red-600">
                                                <fmt:formatNumber value="${product.salePrice}" type="number" groupingUsed="true"/>đ
                                            </span>
                                            <c:if test="${product.originalPrice > product.salePrice}">
                                                <span class="text-sm text-gray-400 line-through ml-2">
                                                    <fmt:formatNumber value="${product.originalPrice}" type="number" groupingUsed="true"/>đ
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="flex flex-col items-center justify-center py-32">
                        <span class="text-6xl mb-4">🛒</span>
                        <p class="text-gray-600 text-sm">Chưa có sản phẩm nào</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <!-- How It Works Section -->
    <section class="py-10 bg-gray-900 text-white relative overflow-hidden">
        <div class="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="mb-8 text-center">
                <div class="inline-block px-2.5 py-1 bg-orange-500/20 border-l-4 border-orange-500 mb-2">
                    <span class="text-[10px] font-bold text-orange-400 uppercase tracking-wider">
                        Cách thức hoạt động
                    </span>
                </div>
                <h2 class="text-xl md:text-2xl font-black text-white mb-2">
                    CHỈ CẦN{" "}
                    <span class="bg-gradient-to-r from-orange-400 to-amber-400 bg-clip-text text-transparent">
                        BA BƯỚC
                    </span>
                </h2>
            </div>

            <div class="grid md:grid-cols-3 gap-6 lg:gap-8">
                <div class="relative bg-gray-800/50 backdrop-blur-sm border-2 border-gray-700 p-6 hover:border-emerald-500 transition-all duration-500 group">
                    <div class="absolute -top-4 -right-2 text-[80px] font-black text-gray-700/30 leading-none select-none">01</div>
                    <div class="relative z-10">
                        <div class="w-14 h-14 bg-gradient-to-br from-emerald-500 to-teal-600 flex items-center justify-center mb-4">
                            <span class="text-2xl">🛍️</span>
                        </div>
                        <h3 class="text-xl font-black text-white mb-3">Chọn sản phẩm</h3>
                        <p class="text-sm text-gray-400">Duyệt và thêm sản phẩm yêu thích vào giỏ hàng</p>
                    </div>
                </div>

                <div class="relative bg-gray-800/50 backdrop-blur-sm border-2 border-gray-700 p-6 hover:border-emerald-500 transition-all duration-500 group">
                    <div class="absolute -top-4 -right-2 text-[80px] font-black text-gray-700/30 leading-none select-none">02</div>
                    <div class="relative z-10">
                        <div class="w-14 h-14 bg-gradient-to-br from-emerald-500 to-teal-600 flex items-center justify-center mb-4">
                            <span class="text-2xl">⏰</span>
                        </div>
                        <h3 class="text-xl font-black text-white mb-3">Đặt hàng</h3>
                        <p class="text-sm text-gray-400">Thanh toán dễ dàng với nhiều phương thức</p>
                    </div>
                </div>

                <div class="relative bg-gray-800/50 backdrop-blur-sm border-2 border-gray-700 p-6 hover:border-emerald-500 transition-all duration-500 group">
                    <div class="absolute -top-4 -right-2 text-[80px] font-black text-gray-700/30 leading-none select-none">03</div>
                    <div class="relative z-10">
                        <div class="w-14 h-14 bg-gradient-to-br from-emerald-500 to-teal-600 flex items-center justify-center mb-4">
                            <span class="text-2xl">🚚</span>
                        </div>
                        <h3 class="text-xl font-black text-white mb-3">Nhận hàng</h3>
                        <p class="text-sm text-gray-400">Giao hàng nhanh trong 2 giờ tại nội thành</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <jsp:include page="../common/footer.jsp" />
    
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>
