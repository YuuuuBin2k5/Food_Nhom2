package com.ecommerce.entity;

public enum ProductCategory {
    VEGETABLES("Rau củ quả", "🥬"),
    FRUITS("Trái cây", "🍎"),
    MEAT("Thịt tươi", "🥩"),
    SEAFOOD("Hải sản", "🦐"),
    DAIRY("Sữa & Phô mai", "🥛"),
    BAKERY("Bánh mì & Bánh ngọt", "🥖"),
    SNACKS("Snack & Đồ ăn vặt", "🍿"),
    BEVERAGES("Đồ uống", "🥤"),
    FROZEN("Thực phẩm đông lạnh", "🧊"),
    CANNED("Đồ hộp", "🥫"),
    CONDIMENTS("Gia vị & Nước sốt", "🧂"),
    OTHER("Khác", "📦");

    private final String displayName;
    private final String emoji;

    ProductCategory(String displayName, String emoji) {
        this.displayName = displayName;
        this.emoji = emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDisplayNameWithEmoji() {
        return emoji + " " + displayName;
    }
}
