

public class Book {
    private final String name;
    private final String author;
    private final String category;
    private final String condition;
    private final String price;
    private final String imageName;

    public Book(String name, String author, String category, String condition, String price, String imageName) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.condition = condition;
        this.price = price;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public String getPrice() {
        return price;
    }

    public String getImageName() {
        return imageName;
    }

    public String toFileFormat() {
        return String.join(";", name, author, category, condition, price, imageName);
    }
}