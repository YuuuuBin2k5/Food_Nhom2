import React from "react";
import { Input } from "@heroui/react";

const MysicInput = ({ label, className = "", ...props }) => {
  return (
    <Input
      label={label}
      labelPlacement="outside"
      variant="bordered"
      radius="md"
      classNames={{
        mainWrapper: "mt-2",
        label: [
          "absolute z-20",
          "-top-2 left-3",
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
          "px-4",
        ],
        input: [
          "text-[#0f172a]",
          "text-base",
          "placeholder:text-[#94a3b8]",
          "h-full",
        ],
      }}
      {...props}
    />
  );
};

export default MysicInput;
