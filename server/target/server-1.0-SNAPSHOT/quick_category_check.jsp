<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ page import="com.ecommerce.entity.*" %>
        <%@ page import="com.ecommerce.util.DBUtil" %>
            <%@ page import="jakarta.persistence.*" %>
                <%@ page import="java.util.*" %>

                    <!DOCTYPE html>
                    <html>

                    <head>
                        <title>Quick Category Check</title>
                        <style>
                            body {
                                font-family: Arial;
                                padding: 20px;
                                background: #f5f5f5;
                            }

                            .summary {
                                display: grid;
                                grid-template-columns: repeat(3, 1fr);
                                gap: 15px;
                                margin: 20px 0;
                            }

                            .card {
                                background: white;
                                padding: 15px;
                                border-radius: 8px;
                                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                            }

                            .card.empty {
                                background: #ffebee;
                                border-left: 4px solid #f44336;
                            }

                            .card.ok {
                                background: #e8f5e9;
                                border-left: 4px solid #4caf50;
                            }

                            .count {
                                font-size: 32px;
                                font-weight: bold;
                                color: #333;
                            }

                            .label {
                                font-size: 14px;
                                color: #666;
                                margin-top: 5px;
                            }

                            .emoji {
                                font-size: 24px;
                            }
                        </style>
                    </head>

                    <body>
                        <h1>📊 Category Quick Check</h1>

                        <% EntityManager em=DBUtil.getEmFactory().createEntityManager(); try { out.println("<div
                            class='summary'>");

                            for (ProductCategory cat : ProductCategory.values()) {
                            Long count = em.createQuery(
                            "SELECT COUNT(p) FROM Product p WHERE p.category = :cat AND p.status = 'ACTIVE'",
                            Long.class
                            )
                            .setParameter("cat", cat)
                            .getSingleResult();

                            String cardClass = count > 0 ? "card ok" : "card empty";

                            out.println("<div class='" + cardClass + "'>");
                                out.println("<div class='emoji'>" + cat.getEmoji() + "</div>");
                                out.println("<div class='count'>" + count + "</div>");
                                out.println("<div class='label'>" + cat.getDisplayName() + "</div>");
                                out.println("</div>");
                            }

                            out.println("</div>");

                            // Check for NULL categories
                            Long nullCount = em.createQuery(
                            "SELECT COUNT(p) FROM Product p WHERE p.category IS NULL AND p.status = 'ACTIVE'",
                            Long.class
                            ).getSingleResult();

                            if (nullCount > 0) {
                            out.println("<div class='card empty' style='margin-top: 20px;'>");
                                out.println("<div class='count'>" + nullCount + "</div>");
                                out.println("<div class='label'>⚠️ Products WITHOUT Category (NULL)</div>");
                                out.println("</div>");
                            }

                            } finally {
                            em.close();
                            }
                            %>

                            <h2>💡 Giải pháp:</h2>
                            <ul>
                                <li>Nếu thấy <strong>0</strong> ở một category → Cần tạo products mới hoặc update
                                    existing products</li>
                                <li>Nếu có products NULL → Chạy <code>CategoryUpdater.java</code> hoặc
                                    <code>update_categories.sql</code></li>
                            </ul>

                    </body>

                    </html>