package com.ecommerce.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayUtil {

    // VNPay Configuration (hardcoded for simplicity)
    private static final String TMN_CODE = "NVZY5W6Q";
    private static final String HASH_SECRET = "8BX47HMR3WR8FO3EN91PGGO72NQAWO5I";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String RETURN_URL = "https://foodrescueweb.onrender.com/vnpay";
    
    /**
     * Generate HMAC SHA512 hash
     */
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC SHA512", e);
        }
    }
    
    /**
     * Build VNPay payment URL
     */
    public static String buildPaymentUrl(String orderId, long amount, String orderInfo, String ipAddress) {
        try {
            Map<String, String> vnpParams = new HashMap<>();
            
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", TMN_CODE);
            vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay requires amount in smallest unit (VND * 100)
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", RETURN_URL);
            vnpParams.put("vnp_IpAddr", ipAddress);
            
            // Create date in format yyyyMMddHHmmss (Vietnam timezone)
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            String vnpCreateDate = String.format("%04d%02d%02d%02d%02d%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
            );
            vnpParams.put("vnp_CreateDate", vnpCreateDate);
            
            // Expire after 15 minutes
            calendar.add(Calendar.MINUTE, 15);
            String vnpExpireDate = String.format("%04d%02d%02d%02d%02d%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
            );
            vnpParams.put("vnp_ExpireDate", vnpExpireDate);
            
            
            
            // Sort parameters by key
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            // Build hash data and query string
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            for (String fieldName : fieldNames) {
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Build hash data
                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    
                    // Build query string
                    if (query.length() > 0) {
                        query.append('&');
                    }
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                         .append('=')
                         .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                }
            }
            
            // Generate secure hash
            String vnpSecureHash = hmacSHA512(HASH_SECRET, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnpSecureHash);
            
            return VNP_URL + "?" + query.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Error building VNPay payment URL", e);
        }
    }
    
    /**
     * Validate VNPay callback signature
     */
    public static boolean validateSignature(Map<String, String> params, String inputHash) {
        try {
            // Remove hash from params
            Map<String, String> vnpParams = new HashMap<>(params);
            vnpParams.remove("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHashType");
            
            // Sort parameters
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            // Build hash data
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    if (hashData.length() > 0) {
                        hashData.append('&');
                    }
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                }
            }
            
            String calculatedHash = hmacSHA512(HASH_SECRET, hashData.toString());
            return calculatedHash.equals(inputHash);
            
        } catch (Exception e) {
            return false;
        }
    }
}
