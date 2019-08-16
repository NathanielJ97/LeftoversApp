//////////////////////////////////////////////////
//CMP3034M - Mobile Computing Assignment Item 1//
//Nathaniel Josephs - JOS13502565///////////////
//Leftovers App - Recipe.cs////////////////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

//User-defined class that holds and describes a recipe object that can be returned from the API
public class Recipe {
    private String id;
    private String title;
    private String publisher;
    private String imageURL;
    private String[] ingredients;
    private String originalURL;

    public Recipe(String id, String title, String publisher, String imageURL, String[] ingredients, String originalURL) {
        this.id = id;
        this.title = title;
        this.publisher = publisher;
        this.imageURL = imageURL;
        this.ingredients = ingredients;
        this.originalURL = originalURL;
    }

    //Getter and Setter Methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients[]) {
        this.ingredients = ingredients;
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public void setOriginalURL(String originalURL) {
        this.originalURL = originalURL;
    }
}
