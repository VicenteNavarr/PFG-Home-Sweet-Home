package homeSweetHome.model;

import java.util.Objects;

/**
 * Modelo que representa una comida semanal.
 */
public class Meal {

    private int id; // ID único de la comida
    private String dayOfWeek; // Día de la semana
    private int recipeId; // ID de la receta
    private String recipeName; // Nombre de la receta
    private String category; // Categoría de la receta
    private byte[] photo; // Foto de la receta
    private int groupId; // ID del grupo

    // Constructor completo
    public Meal(int id, String dayOfWeek, int recipeId, String recipeName, String category, byte[] photo, int groupId) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.category = category;
        this.photo = photo;
        this.groupId = groupId;
    }

    // Constructor vacío
    public Meal() {}

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", recipeId=" + recipeId +
                ", recipeName='" + recipeName + '\'' +
                ", category='" + category + '\'' +
                ", groupId=" + groupId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return id == meal.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

