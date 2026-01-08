// Auto-hide on scroll
let lastScrollY = 0;
const sidebar = document.getElementById('sidebar');

window.addEventListener('scroll', () => {
    const currentScrollY = window.scrollY;

    // Add/remove scrolled class
    if (currentScrollY < 10) {
        sidebar.classList.remove('scrolled');
    } else {
        sidebar.classList.add('scrolled');
    }

    lastScrollY = currentScrollY;
}, { passive: true });

// Toggle dropdown
function toggleDropdown() {
    const dropdown = document.getElementById('profileDropdown');
    dropdown.classList.toggle('show');
}

// Close dropdown when clicking outside
document.addEventListener('click', (e) => {
    const dropdown = document.getElementById('profileDropdown');
    const profileBtn = document.querySelector('.profile-btn');

    if (dropdown && profileBtn && !profileBtn.contains(e.target) && !dropdown.contains(e.target)) {
        dropdown.classList.remove('show');
    }
});

// Toggle mobile menu
function toggleMobileMenu() {
    const mobileMenu = document.getElementById('mobileMenu');
    const menuIcon = document.getElementById('menuIcon');
    const isOpen = mobileMenu.classList.contains('show');

    if (!isOpen) {
        mobileMenu.classList.add('show');
        menuIcon.setAttribute('d', 'M6 18L18 6M6 6l12 12');
    } else {
        mobileMenu.classList.remove('show');
        menuIcon.setAttribute('d', 'M4 6h16M4 12h16M4 18h16');
    }
}

// Close mobile menu when clicking outside
document.addEventListener('click', (e) => {
    const mobileMenu = document.getElementById('mobileMenu');
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');

    if (mobileMenu && mobileMenuBtn && 
        !mobileMenuBtn.contains(e.target) && 
        !mobileMenu.contains(e.target) &&
        mobileMenu.classList.contains('show')) {
        mobileMenu.classList.remove('show');
        document.getElementById('menuIcon').setAttribute('d', 'M4 6h16M4 12h16M4 18h16');
    }
});

// Close mobile menu on window resize
window.addEventListener('resize', () => {
    if (window.innerWidth >= 768) {
        const mobileMenu = document.getElementById('mobileMenu');
        if (mobileMenu) {
            mobileMenu.classList.remove('show');
            document.getElementById('menuIcon').setAttribute('d', 'M4 6h16M4 12h16M4 18h16');
        }
    }
});
