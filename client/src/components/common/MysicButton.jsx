import React from "react";
import { Button as HeroButton } from "@heroui/react";

const MysicButton = ({ children, className = "", style = {}, ...props }) => {
  return (
    <HeroButton
      size="lg"
      radius="md"
      className={`mt-2 w-40 h-10 rounded-2xl font-bold shadow-lg transition-transform active:scale-[0.98] cursor-pointer ${className}`}
      style={{
        backgroundImage:
          "linear-gradient(90deg,#FF6B6B 0%,#FF8E53 65%,#FFC75F 100%)",
        color: "#fff1f2",
        border: "none",
        boxShadow: "0 8px 30px -8px rgba(255,107,107,0.45)",
        ...style,
      }}
      {...props}
    >
      {children}
    </HeroButton>
  );
};

export default MysicButton;
