package mg.nexthope.point_de_vente_app.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.models.Ticket;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Ticket> tickets;
    public HashMap<Ticket, Integer> cartItems = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CartListAdapter(Context context, HashMap<Ticket, Integer> cartItems) {
        this.context = context;
        this.cartItems = cartItems;

        Map<Ticket, Integer> result = cartItems.entrySet()
                .stream()
                .filter(map -> map.getValue() != 0)
                .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));

        Set<Ticket> keys = result.keySet();
        tickets = new ArrayList<>(keys);
    }

    @NonNull
    @Override
    public CartListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        return new CartListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartListAdapter.ViewHolder holder, int position) {
        holder.getTicketLabel().setText(tickets.get(position).getLabel());
        holder.getTicketPrice().setText((tickets.get(position).getPrice() * cartItems.get(tickets.get(position))) + " " + tickets.get(position).getCurrency());
        holder.getTicketQuantity().setText("Quantité: " + cartItems.get(tickets.get(position)).toString());
        // holder.showQuantityDialog();
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView ticketLabel;
        private TextView ticketPrice;
        private TextView ticketQuantity;

        private TicketListAdapter.OnTicketListListener onTicketListListener;

        public  ViewHolder(View itemView) {
            super(itemView);

            ticketLabel = itemView.findViewById(R.id.cart_item_label);
            ticketPrice = itemView.findViewById(R.id.cart_item_price);
            ticketQuantity = itemView.findViewById(R.id.cart_item_quantity);
        }

        public TextView getTicketLabel() {
            return ticketLabel;
        }

        public TextView getTicketPrice() {
            return ticketPrice;
        }

        public TextView getTicketQuantity() {
            return ticketQuantity;
        }
    }
}
