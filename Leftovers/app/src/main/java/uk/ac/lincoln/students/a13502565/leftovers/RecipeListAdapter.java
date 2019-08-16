//////////////////////////////////////////////////
//CMP3034M - Mobile Computing Assignment Item 1//
//Nathaniel Josephs - JOS13502565///////////////
//Leftovers App - RecipeListAdapter.cs/////////
//////////////////////////////////////////////

package uk.ac.lincoln.students.a13502565.leftovers;

import android.content.Context;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {

    private static final String TAG = "RecipeListAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    //Holds variables in view
    private static class ViewHolder {
        TextView id;
        TextView title;
        TextView publisher;
        ImageView image;
    }

    /** Default constructor for RecipeListAdapter
     *  @param context
     * @param resource
     * @param objects
     */
    public RecipeListAdapter(Context context, int resource, ArrayList<Recipe> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Method for setting up the Universal Image Loader
        SetupImageLoader();

        //Get recipe info
        String id = getItem(position).getId();
        String title = getItem(position).getTitle();
        String publisher = getItem(position).getPublisher();
        String imageURL = getItem(position).getImageURL();
        String[] ingredients = getItem(position).getIngredients();
        String originalURL = getItem(position).getOriginalURL();

        //Create recipe object
        Recipe recipe = new Recipe(id, title, publisher, imageURL, ingredients, originalURL);

        //Create animation view result
        final View result;

        //Viewholder object
        ViewHolder vHold;

        //Load a few list entries at a time rather than the whole list
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            vHold = new ViewHolder();
            vHold.id = convertView.findViewById(R.id.textView1);
            vHold.title = convertView.findViewById(R.id.textView2);
            vHold.publisher = convertView.findViewById(R.id.textView3);
            //Initialise the Image view
            vHold.image = convertView.findViewById(R.id.recipeImage);

            result = convertView;
            convertView.setTag(vHold);
        }
        else
        {
            vHold = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Loading Animation
        //If the position is greater than last position, use down animation otherwise, use up animation
        Animation anim = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.loading_d_anim : R.anim.loading_u_anim);
        result.startAnimation(anim);
        lastPosition = position;

        //Create the ImageLoader object
        ImageLoader imageLoader = ImageLoader.getInstance();

        //Define the image that will be loaded should the ImageLoader fail to download from the imageURL
        int fallBackImage = mContext.getResources().getIdentifier("@drawable/image_failed", null, mContext.getPackageName());

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallBackImage)
                .showImageOnFail(fallBackImage)
                .showImageOnLoading(fallBackImage).build();

        //Display the image
        imageLoader.displayImage(imageURL, vHold.image, options);

        //Apply obtained data to the UI
        vHold.id.setText(id);
        vHold.title.setText(title);
        vHold.publisher.setText(publisher);

        return convertView;
    }

    private void SetupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}
