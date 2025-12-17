package com.ecommerce.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.Collectors;

import com.ecommerce.dto.RegisterDTO;
import com.ecommerce.service.RegisterService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/api/register"})
public class RegisterServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final RegisterService registerService = new RegisterService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        setAccessControlHeaders(response);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter();
             BufferedReader reader = request.getReader()) {

            JsonObject jsonResponse = new JsonObject();

            try {
                RegisterDTO dto = gson.fromJson(reader, RegisterDTO.class);

                // Server-side validation using Jakarta Bean Validation
                try (ValidatorFactory vf = Validation.buildDefaultValidatorFactory()) {
                    Validator validator = vf.getValidator();
                    Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        java.util.Map<String, java.util.List<String>> errorsMap = violations.stream()
                            .collect(Collectors.groupingBy(v -> v.getPropertyPath().toString(),
                                Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())));

                        JsonObject err = new JsonObject();
                        err.addProperty("success", false);
                        err.addProperty("message", "Validation failed");
                        err.add("errors", gson.toJsonTree(errorsMap));
                        out.print(gson.toJson(err));
                        return;
                    }
                }

                registerService.registerUser(
                                    dto.getFullName(),
                                    dto.getEmail(),
                                    dto.getPassword(),
                                    dto.getPhoneNumber(),
                                    dto.getRole(),
                                    dto.getShopName()
                                );



                jsonResponse.addProperty("status", "success");
                jsonResponse.addProperty("message", "Đăng ký thành công! Vui lòng đăng nhập.");
                response.setStatus(HttpServletResponse.SC_CREATED);

            } catch (Exception e) {
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            out.print(gson.toJson(jsonResponse));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
    }
}