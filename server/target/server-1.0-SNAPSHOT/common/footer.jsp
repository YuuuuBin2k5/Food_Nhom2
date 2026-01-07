<%@ page contentType="text/html;charset=UTF-8" %>

<footer style="background-color: #2d3748; color: #e2e8f0;">
    <!-- Decorative top border -->
    <div style="height: 3px; background-color: #4299e1;"></div>

    <div style="max-width: 80rem; margin: 0 auto; padding: 3rem 1rem;">
        <div style="display: grid; grid-template-columns: repeat(1, 1fr); gap: 2rem; margin-bottom: 2rem;">
            <!-- Brand -->
            <div style="display: grid; grid-template-columns: repeat(1, 1fr); gap: 2rem;">
                <div>
                    <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
                        <div style="width: 2.5rem; height: 2.5rem; background-color: #4299e1; display: flex; align-items: center; justify-content: center; border-radius: 4px;">
                            <span style="font-size: 1.5rem;">🛒</span>
                        </div>
                        <div>
                            <span style="display: block; font-weight: 700; font-size: 1.5rem; color: #ffffff;">FoodRescue</span>
                            <span style="display: block; font-size: 0.75rem; color: #a0aec0; margin-top: -0.25rem;">Smart Shopping Platform</span>
                        </div>
                    </div>
                    <p style="font-size: 0.875rem; line-height: 1.6; color: #cbd5e0; margin-bottom: 1rem;">
                        Nền tảng mua sắm trực tuyến hiện đại, kết nối người mua và người bán.
                    </p>
                </div>

                <!-- Links Grid -->
                <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 2rem;">
                    <!-- Về chúng tôi -->
                    <div>
                        <h3 style="color: #ffffff; font-weight: 600; font-size: 1rem; margin-bottom: 1rem;">
                            Về chúng tôi
                        </h3>
                        <ul style="list-style: none; padding: 0; margin: 0;">
                            <li style="margin-bottom: 0.5rem;">
                                <a href="#" style="color: #cbd5e0; text-decoration: none; font-size: 0.875rem;">Giới thiệu</a>
                            </li>
                            <li style="margin-bottom: 0.5rem;">
                                <a href="#" style="color: #cbd5e0; text-decoration: none; font-size: 0.875rem;">Đối tác</a>
                            </li>
                            <li style="margin-bottom: 0.5rem;">
                                <a href="#" style="color: #cbd5e0; text-decoration: none; font-size: 0.875rem;">Tin tức</a>
                            </li>
                        </ul>
                    </div>

                    <!-- Hỗ trợ -->
                    <div>
                        <h3 style="color: #ffffff; font-weight: 600; font-size: 1rem; margin-bottom: 1rem;">
                            Hỗ trợ
                        </h3>
                        <ul style="list-style: none; padding: 0; margin: 0;">
                            <li style="margin-bottom: 0.5rem;">
                                <a href="#" style="color: #cbd5e0; text-decoration: none; font-size: 0.875rem;">Câu hỏi thường gặp</a>
                            </li>
                            <li style="margin-bottom: 0.5rem;">
                                <a href="#" style="color: #cbd5e0; text-decoration: none; font-size: 0.875rem;">Chính sách đổi trả</a>
                            </li>
                            <li style="margin-bottom: 0.5rem;">
                                <a href="#" style="color: #cbd5e0; text-decoration: none; font-size: 0.875rem;">Điều khoản sử dụng</a>
                            </li>
                        </ul>
                    </div>

                    <!-- Liên hệ -->
                    <div>
                        <h3 style="color: #ffffff; font-weight: 600; font-size: 1rem; margin-bottom: 1rem;">
                            Liên hệ
                        </h3>
                        <ul style="list-style: none; padding: 0; margin: 0;">
                            <li style="display: flex; align-items: flex-start; gap: 0.5rem; margin-bottom: 0.75rem; font-size: 0.875rem; color: #cbd5e0;">
                                <span>📧</span>
                                <span>support@foodrescue.vn</span>
                            </li>
                            <li style="display: flex; align-items: flex-start; gap: 0.5rem; margin-bottom: 0.75rem; font-size: 0.875rem; color: #cbd5e0;">
                                <span>📞</span>
                                <span>1900 xxxx</span>
                            </li>
                            <li style="display: flex; align-items: flex-start; gap: 0.5rem; margin-bottom: 0.75rem; font-size: 0.875rem; color: #cbd5e0;">
                                <span>📍</span>
                                <span>TP.HCM, Việt Nam</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bottom bar -->
        <div style="padding-top: 1.5rem; border-top: 1px solid #4a5568; text-align: center;">
            <p style="font-size: 0.875rem; color: #a0aec0; margin: 0;">
                © 2024 FoodRescue Platform. All rights reserved.
            </p>
        </div>
    </div>
</footer>

<style>
    footer a:hover {
        color: #4299e1;
    }
    
    @media (min-width: 768px) {
        footer > div > div:first-child {
            grid-template-columns: repeat(2, 1fr);
        }
    }
    
    @media (min-width: 1024px) {
        footer > div > div:first-child {
            grid-template-columns: 1fr 2fr;
        }
    }
</style>
