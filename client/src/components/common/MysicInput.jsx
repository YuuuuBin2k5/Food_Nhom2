import React, { useState } from "react";
import { Input } from "@heroui/react";

const MysicInput = ({ label, type = "text", className = "", ...props }) => {
  const [showPassword, setShowPassword] = useState(false);
  const isPassword = type === "password";

  return (
    <div className={`relative ${className}`}>
      <Input
        label={label}
        labelPlacement="outside"
        variant="bordered"
        radius="md"
        type={isPassword && showPassword ? "text" : type}
        classNames={{
          mainWrapper: "mt-1",
          errorMessage: ["text-sm", "text-red-500"],
          label: [
            "absolute z-20",
            "-top-2 left-2",
            "-translate-y-1/2",
            "bg-[#FFF9F0]",
            "text-[#334155]",
            "text-sm font-bold",
            "px-1",
          ],
          inputWrapper: [
            "bg-white",
            "border-[#E7DACE]",
            "hover:border-[#E7DACE]",
            "focus-within:!border-[#10B981]",
            "focus-within:!shadow-[0_0_12px_-3px_rgba(16,185,129,0.18)]",
            "h-10",
          ],
          input: [
            "text-[#0f172a]",
            "text-base",
            "text-[20px]",
            "placeholder:text-[#94a3b8]",
            "h-full",
            "p-2",
          ],
        }}
        {...props}
      />

      {isPassword && (
        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
        >
          {showPassword ? (
            // Icon mắt mở
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5 mt-2"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M13.875 18.825A10.05 10.05 0 0112 19c-5.523 0-10-4.477-10-10 0-1.112.17-2.18.487-3.19M21.5 12a10.05 10.05 0 00-.487-3.19M3 3l18 18"
              />
            </svg>
          ) : (
            // Icon mắt đóng
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
              />
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
              />
            </svg>
          )}
        </button>
      )}
    </div>
  );
};

export default MysicInput;
