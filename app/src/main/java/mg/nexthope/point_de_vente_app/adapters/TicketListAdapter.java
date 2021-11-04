package mg.nexthope.point_de_vente_app.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.activities.TicketListActivity;
import mg.nexthope.point_de_vente_app.models.Ticket;

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Ticket> tickets;
    private OnTicketListListener onTicketListListener;
    private TicketListActivity ticketListActivity;
    public HashMap<Ticket, Integer> cartItems = new HashMap<>();

    public TicketListAdapter(TicketListActivity ticketListActivity, Context context, ArrayList<Ticket> tickets, TicketListAdapter.OnTicketListListener onTicketListListener) {
        this.context = context;
        this.tickets = tickets;
        this.onTicketListListener = onTicketListListener;
        this.ticketListActivity = ticketListActivity;

        for (Ticket ticket: tickets) {
            cartItems.put(ticket, 0);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ticket_item, parent, false);

        return new TicketListAdapter.ViewHolder(view, onTicketListListener);
    }

    @Override
    public void onBindViewHolder(TicketListAdapter.ViewHolder holder, int position) {
        holder.getTicketLabel().setText(tickets.get(position).getLabel());
        holder.getTicketPrice().setText(tickets.get(position).getPrice() + " " + tickets.get(position).getCurrency());
        holder.getTicketDescription().setText(tickets.get(position).getDescription());
        holder.getTicketId().setText(String.valueOf(tickets.get(position).getId()));

        // holder.showQuantityDialog();
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView ticketId;
        private TextView ticketLabel;
        private TextView ticketPrice;
        private TextView ticketDescription;
        private ImageButton addTicket;
        private TextView quantity;
        private ImageButton removeTicket;
        private Button addToCartButton;

        private TicketListAdapter.OnTicketListListener onTicketListListener;

        public ViewHolder(View itemView, TicketListAdapter.OnTicketListListener onTicketListListener) {
            super(itemView);

            ticketId = itemView.findViewById(R.id.ticket_item_ticket_id);
            ticketLabel = itemView.findViewById(R.id.ticket_item_label);
            ticketPrice = itemView.findViewById(R.id.ticket_item_price);
            ticketDescription = itemView.findViewById(R.id.ticket_item_description);
            addTicket = itemView.findViewById(R.id.ticket_item_add);
            removeTicket = itemView.findViewById(R.id.ticket_item_remove);
            quantity = itemView.findViewById(R.id.ticket_item_quantity);

            addTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(quantity.getText().toString()) != 10) {
                        int newQuantity = Integer.parseInt(quantity.getText().toString()) + 1;
                        double newPrice = newQuantity * Double.parseDouble(ticketPrice.getText().toString().split(" ")[0]);
                        quantity.setText(String.valueOf(newQuantity));
                        ticketListActivity.total.setText((Double.parseDouble(ticketListActivity.total.getText().toString()) + Double.parseDouble(ticketPrice.getText().toString().split(" ")[0])) + "");
                        cartItems.put(tickets.get(getAdapterPosition()), newQuantity);
                        // ticketPrice.setText(String.valueOf(newPrice) + " Ar");
                    }
                }
            });

            removeTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt(quantity.getText().toString()) != 0) {
                        int newQuantity = Integer.parseInt(quantity.getText().toString()) - 1;
                        double newPrice = newQuantity * Double.parseDouble(ticketPrice.getText().toString().split(" ")[0]);
                        quantity.setText(String.valueOf(newQuantity));
                        ticketListActivity.total.setText((Double.parseDouble(ticketListActivity.total.getText().toString()) - Double.parseDouble(ticketPrice.getText().toString().split(" ")[0])) + "");
                        cartItems.put(tickets.get(getAdapterPosition()), newQuantity);
                    }
                }
            });
            /*addToCartButton = itemView.findViewById(R.id.add_to_cart_button);

            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQuantityDialog();
                }
            });*/

            this.onTicketListListener = onTicketListListener;
            // itemView.setOnClickListener(this);
        }

        public void showQuantityDialog() {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.ticket_quantity_dialog);

            NumberPicker quantityPicker = dialog.findViewById(R.id.ticket_quantity_dialog_number);
            quantityPicker.setMinValue(1);
            quantityPicker.setMaxValue(10);

            TextView amount = dialog.findViewById(R.id.ticket_quantity_dialog_amount);
            Button addButton = dialog.findViewById(R.id.ticket_quantity_dialog_add);

            quantityPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    double val = 2000 * newVal;
                    amount.setText("" + val);
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setAttributes(lp);
            dialog.show();

        }

        public TextView getTicketLabel() {
            return ticketLabel;
        }

        public TextView getTicketPrice() {
            return ticketPrice;
        }

        public TextView getTicketDescription() {
            return ticketDescription;
        }

        public OnTicketListListener getOnTicketListListener() {
            return onTicketListListener;
        }

        public TextView getTicketId() {
            return ticketId;
        }

        @Override
        public void onClick(View v) {
            onTicketListListener.onTicketListListener(getAdapterPosition());
        }
    }

    public interface OnTicketListListener {
        void onTicketListListener(int position);
    }
}
