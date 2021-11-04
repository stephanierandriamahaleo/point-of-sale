package mg.nexthope.point_de_vente_app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.models.Event;
import mg.nexthope.point_de_vente_app.models.Shop;
import retrofit2.http.Url;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private ArrayList<Event> events;
    private OnEventListListener onEventListListener;
    private Context context;

    public EventListAdapter(ArrayList<Event> events, EventListAdapter.OnEventListListener onEventListListener, Context context) {
        this.events = events;
        this.onEventListListener = onEventListListener;
        this.context = context;
    }

    @NonNull
    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);

        return new EventListAdapter.ViewHolder(view, onEventListListener);
    }

    @Override
    public void onBindViewHolder(EventListAdapter.ViewHolder holder, int position) {
        holder.getEventTitle().setText(events.get(position).getTitle());
        holder.getEventPlace().setText(" " + events.get(position).getPlace());
        holder.getEventDate().setText(" " + events.get(position).getDate());

        Picasso.with(context)
                .load(events.get(position).getPictureUrl())
                .resize(200, 200)
                .centerCrop()
                .into(holder.getEventImageView());
        // holder.getEventTicketPriceRange().setText(events.get(position).getTicketPriceRange());

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView eventTitle;
        private TextView eventDate;
        private TextView eventPlace;
        private ImageView eventImageView;

        private EventListAdapter.OnEventListListener onEventListListener;

        public ViewHolder(View itemView, EventListAdapter.OnEventListListener onEventListListener) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.event_item_event_title);
            eventDate = itemView.findViewById(R.id.event_item_event_date);
            eventPlace = itemView.findViewById(R.id.event_item_event_place);
            eventImageView = itemView.findViewById(R.id.event_item_image);

            this.onEventListListener = onEventListListener;
            itemView.setOnClickListener(this);
        }

        public TextView getEventTitle() {
            return eventTitle;
        }

        public TextView getEventDate() {
            return eventDate;
        }

        public TextView getEventPlace() {
            return eventPlace;
        }

        public ImageView getEventImageView() {
            return eventImageView;
        }

        public EventListAdapter.OnEventListListener getOnEventListListener() {
            return onEventListListener;
        }

        @Override
        public void onClick(View v) {
            onEventListListener.onEventClick(getAdapterPosition());
        }
    }

    public interface OnEventListListener {
        void onEventClick(int position);
    }
}
