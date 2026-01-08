// Main JavaScript file

// Toast notification system
const Toast = {
    container: null,
    
    init() {
        this.container = document.getElementById('toast-container');
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.id = 'toast-container';
            this.container.style.cssText = 'position: fixed; top: 1rem; right: 1rem; z-index: 9999;';
            document.body.appendChild(this.container);
        }
    },
    
    show(message, type = 'info', duration = 3000) {
        this.init();
        
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.style.cssText = `
            background: white;
            padding: 1rem 1.5rem;
            border-radius: 0.75rem;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            margin-bottom: 0.5rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            min-width: 300px;
            animation: slideIn 0.3s ease;
            border-left: 4px solid ${this.getColor(type)};
        `;
        
        const icon = this.getIcon(type);
        const iconEl = document.createElement('span');
        iconEl.style.fontSize = '1.5rem';
        iconEl.textContent = icon;
        
        const messageEl = document.createElement('span');
        messageEl.style.cssText = 'flex: 1; color: #0f172a; font-weight: 500;';
        messageEl.textContent = message;
        
        const closeBtn = document.createElement('button');
        closeBtn.textContent = '✕';
        closeBtn.style.cssText = `
            background: none;
            border: none;
            color: #64748b;
            cursor: pointer;
            font-size: 1.25rem;
            padding: 0;
            width: 1.5rem;
            height: 1.5rem;
            display: flex;
            align-items: center;
            justify-content: center;
        `;
        closeBtn.onclick = () => this.remove(toast);
        
        toast.appendChild(iconEl);
        toast.appendChild(messageEl);
        toast.appendChild(closeBtn);
        
        this.container.appendChild(toast);
        
        if (duration > 0) {
            setTimeout(() => this.remove(toast), duration);
        }
    },
    
    remove(toast) {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    },
    
    getIcon(type) {
        const icons = {
            success: '✅',
            error: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };
        return icons[type] || icons.info;
    },
    
    getColor(type) {
        const colors = {
            success: '#10b981',
            error: '#ef4444',
            warning: '#f59e0b',
            info: '#3b82f6'
        };
        return colors[type] || colors.info;
    },
    
    success(message, duration) {
        this.show(message, 'success', duration);
    },
    
    error(message, duration) {
        this.show(message, 'error', duration);
    },
    
    warning(message, duration) {
        this.show(message, 'warning', duration);
    },
    
    info(message, duration) {
        this.show(message, 'info', duration);
    }
};

// Add toast animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Format price helper
function formatPrice(price) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
}

// Format date helper
function formatDate(dateStr) {
    return new Date(dateStr).toLocaleString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// AJAX helper
const API = {
    async request(url, options = {}) {
        try {
            const response = await fetch(url, {
                ...options,
                headers: {
                    'Content-Type': 'application/json',
                    ...options.headers
                }
            });
            
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Request failed');
            }
            
            return await response.json();
        } catch (error) {
            Toast.error(error.message);
            throw error;
        }
    },
    
    get(url, options) {
        return this.request(url, { ...options, method: 'GET' });
    },
    
    post(url, data, options) {
        return this.request(url, {
            ...options,
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    
    put(url, data, options) {
        return this.request(url, {
            ...options,
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    
    delete(url, options) {
        return this.request(url, { ...options, method: 'DELETE' });
    }
};

// Loading spinner
const Loading = {
    show() {
        let spinner = document.getElementById('loading-spinner');
        if (!spinner) {
            spinner = document.createElement('div');
            spinner.id = 'loading-spinner';
            spinner.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10000;
            `;
            spinner.innerHTML = `
                <div style="
                    width: 64px;
                    height: 64px;
                    border: 4px solid rgba(255, 255, 255, 0.3);
                    border-top-color: #FF6B6B;
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                "></div>
            `;
            document.body.appendChild(spinner);
            
            // Add spin animation
            const spinStyle = document.createElement('style');
            spinStyle.textContent = `
                @keyframes spin {
                    to { transform: rotate(360deg); }
                }
            `;
            document.head.appendChild(spinStyle);
        }
        spinner.style.display = 'flex';
    },
    
    hide() {
        const spinner = document.getElementById('loading-spinner');
        if (spinner) {
            spinner.style.display = 'none';
        }
    }
};

// Form validation helper
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const inputs = form.querySelectorAll('input[required], textarea[required], select[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            isValid = false;
            input.style.borderColor = '#ef4444';
            
            // Remove error styling on input
            input.addEventListener('input', function() {
                this.style.borderColor = '';
            }, { once: true });
        }
    });
    
    if (!isValid) {
        Toast.error('Vui lòng điền đầy đủ thông tin');
    }
    
    return isValid;
}

// Debounce helper
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Modal helpers
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
        document.body.style.overflow = '';
    }
}

// Shorthand functions for common operations
function showToast(message, type = 'info') {
    Toast.show(message, type);
}

function showLoading() {
    Loading.show();
}

function hideLoading() {
    Loading.hide();
}

// API request wrapper with better error handling
async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });
        
        if (!response.ok) {
            let errorMessage = 'Có lỗi xảy ra';
            try {
                const error = await response.json();
                errorMessage = error.message || errorMessage;
            } catch (e) {
                errorMessage = response.statusText || errorMessage;
            }
            throw new Error(errorMessage);
        }
        
        // Check if response has content
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        
        return null;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Format date and time
function formatDateTime(dateStr) {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Format date only
function formatDateOnly(dateStr) {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}

// Confirm dialog
function confirmAction(message) {
    return confirm(message);
}

// Close modal when clicking outside
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal')) {
        e.target.classList.remove('active');
        document.body.style.overflow = '';
    }
});

// Close modal on ESC key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        const activeModal = document.querySelector('.modal.active');
        if (activeModal) {
            activeModal.classList.remove('active');
            document.body.style.overflow = '';
        }
    }
});

// Export to window
window.Toast = Toast;
window.API = API;
window.Loading = Loading;
window.formatPrice = formatPrice;
window.formatDate = formatDate;
window.formatDateTime = formatDateTime;
window.formatDateOnly = formatDateOnly;
window.validateForm = validateForm;
window.debounce = debounce;
window.openModal = openModal;
window.closeModal = closeModal;
window.showToast = showToast;
window.showLoading = showLoading;
window.hideLoading = hideLoading;
window.apiRequest = apiRequest;
window.confirmAction = confirmAction;
