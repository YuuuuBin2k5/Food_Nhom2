<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ page import="com.ecommerce.entity.Product" %>
        <%@ page import="com.ecommerce.entity.ProductCategory" %>
            <%@ page import="com.ecommerce.util.DBUtil" %>
                <%@ page import="jakarta.persistence.*" %>
                    <%@ page import="java.util.List" %>

                        <!DOCTYPE html>
                        <html>

                        <head>
                            <title>Debug Categories</title>
                            <style>
                                body {
                                    font-family: Arial;
                                    padding: 20px;
                                }

                                table {
                                    border-collapse: collapse;
                                    width: 100%;
                                    margin-top: 20px;
                                }

                                th,
                                td {
                                    border: 1px solid #ddd;
                                    padding: 8px;
                                    text-align: left;
                                }

                                th {
                                    background-color: #4CAF50;
                                    color: white;
                                }

                                tr:nth-child(even) {
                                    background-color: #f2f2f2;
                                }

                                .stat {
                                    display: inline-block;
                                    margin: 10px;
                                    padding: 10px 20px;
                                    background: #e3f2fd;
                                    border-radius: 5px;
                                }
                            </style>
                        </head>

                        <body>
                            <h1>🔍 Database Category Debug</h1>

                            <% EntityManager em=null; try { em=DBUtil.getEmFactory().createEntityManager(); // Query all
                                ACTIVE products TypedQuery<Product> query = em.createQuery(
                                "SELECT p FROM Product p LEFT JOIN FETCH p.seller WHERE p.status = 'ACTIVE'",
                                Product.class
                                );
                                List<Product> products = query.getResultList();

                                    out.println("<div class='stat'><strong>Total Active Products:</strong> " +
                                        products.size() + "</div>");

                                    // Count by category
                                    for (ProductCategory cat : ProductCategory.values()) {
                                    long count = products.stream()
                                    .filter(p -> p.getCategory() == cat)
                                    .count();
                                    out.println("<div class='stat'><strong>" + cat.getDisplayName() + ":</strong> " +
                                        count + "</div>");
                                    }

                                    long nullCount = products.stream().filter(p -> p.getCategory() == null).count();
                                    if (nullCount > 0) {
                                    out.println("<div class='stat' style='background: #ffebee;'><strong>NULL
                                            Category:</strong> " + nullCount + "</div>");
                                    }

                                    out.println("<table>");
                                        out.println("<tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Category (Enum)</th>
                                            <th>Display Name</th>
                                            <th>Seller</th>
                                        </tr>");

                                        for (Product p : products) {
                                        out.println("<tr>");
                                            out.println("<td>" + p.getProductId() + "</td>");
                                            out.println("<td>" + p.getName() + "</td>");
                                            out.println("<td>" + (p.getCategory() != null ? p.getCategory().name() :
                                                "<span style='color:red'>NULL</span>") + "</td>");
                                            out.println("<td>" + (p.getCategory() != null ?
                                                p.getCategory().getDisplayName() : "N/A") + "</td>");
                                            out.println("<td>" + (p.getSeller() != null ? p.getSeller().getShopName() :
                                                "N/A") + "</td>");
                                            out.println("</tr>");
                                        }

                                        out.println("</table>");

                                    } catch (Exception e) {
                                    out.println("<h3 style='color:red'>Error: " + e.getMessage() + "</h3>");
                                    e.printStackTrace(new java.io.PrintWriter(out));
                                    } finally {
                                    if (em != null) em.close();
                                    }
                                    %>

                        </body>

                        </html>