<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="content-box">
    <h2>Yêu cầu Hoàn trả / Hoàn tiền</h2>
    <table>
        <thead>
            <tr>
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Lý do</th>
                <th>Trạng thái</th>
                <th>Xử lý</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>#ORD-9981</td>
                <td>Lê Thị C</td>
                <td>Hàng bị vỡ khi giao</td>
                <td><span style="color:orange; font-weight: bold;">Chờ xử lý</span></td>
                <td>
                    <button class="btn btn-green">Chấp nhận</button>
                    <button class="btn btn-red">Từ chối</button>
                </td>
            </tr>
        </tbody>
    </table>
</div>

<div class="content-box">
    <h3>Thống kê nhanh tháng này</h3>
    <ul style="list-style-type: none; padding: 0;">
        <li>✅ Tổng yêu cầu: <b>15</b></li>
        <li>🚀 Đã xử lý: <b>10</b></li>
        <li>⏳ Đang chờ: <b>5</b></li>
    </ul>
</div>