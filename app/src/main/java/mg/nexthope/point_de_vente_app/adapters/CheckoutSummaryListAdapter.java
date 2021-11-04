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

public class CheckoutSummaryListAdapter extends RecyclerView.Adapter<CheckoutSummaryListAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Ticket> tickets;

    public CheckoutSummaryListAdapter(Context context, ArrayList<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public CheckoutSummaryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkout_summary_item, parent, false);

        return new CheckoutSummaryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutSummaryListAdapter.ViewHolder holder, int position) {
        holder.getTicketNumber().setText(tickets.get(position).getNumber());
        holder.getTicketPlace().setText("-");
        holder.getTicketSection().setText("-");
        holder.getTicketPrice().setText(tickets.get(position).getPrice() + "");
        holder.getTicketType().setText(tickets.get(position).getLabel());

        // holder.showQuantityDialog();
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView ticketNumber;
        private TextView ticketPlace;
        private TextView ticketSection;
        private TextView ticketPrice;
        private TextView ticketType;


        public  ViewHolder(View itemView) {
            super(itemView);

            ticketNumber = itemView.findViewById(R.id.checkout_item_ticket_number);
            ticketPlace = itemView.findViewById(R.id.checkout_item_ticket_place);
            ticketSection = itemView.findViewById(R.id.checkout_item_ticket_section);
            ticketPrice = itemView.findViewById(R.id.checkout_item_ticket_price);
            ticketType = itemView.findViewById(R.id.checkout_item_ticket_type);

        }

        public TextView getTicketNumber() {
            return ticketNumber;
        }

        public TextView getTicketPlace() {
            return ticketPlace;
        }

        public TextView getTicketSection() {
            return ticketSection;
        }

        public TextView getTicketPrice() {
            return ticketPrice;
        }

        public TextView getTicketType() {
            return ticketType;
        }
    }
}
