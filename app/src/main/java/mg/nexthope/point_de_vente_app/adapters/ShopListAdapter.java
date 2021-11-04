package mg.nexthope.point_de_vente_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mg.nexthope.point_de_vente_app.R;
import mg.nexthope.point_de_vente_app.models.Shop;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ViewHolder> {
    private ArrayList<Shop> shops;
    private OnShopListListener onShopListListener;

    public ShopListAdapter(ArrayList<Shop> shops, OnShopListListener onShopListListener) {
        this.shops = shops;
        this.onShopListListener = onShopListListener;
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Shop shop = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shop_item, parent, false);
        }

        TextView shopName = convertView.findViewById(R.id.shop_item_shop_name);
        TextView shopRegion = convertView.findViewById(R.id.shop_item_shop_region);
        TextView shopPhone = convertView.findViewById(R.id.shop_item_shop_phone);
        TextView shopOpening = convertView.findViewById(R.id.shop_item_shop_opening);

        shopName.setText(shop.getName());
        shopRegion.setText(shop.getRegion());
        shopPhone.setText(shop.getPhone());
        shopOpening.setText(shop.getOpening());

        return convertView;
    }*/


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item, parent, false);

        return new ViewHolder(view, onShopListListener);
    }

    @Override
    public void onBindViewHolder(ShopListAdapter.ViewHolder holder, int position) {
        holder.getShopName().setText(shops.get(position).getName());
        holder.getShopPhone().setText(!shops.get(position).getPhone().isEmpty() ? " " + shops.get(position).getPhone() : " - ");
        holder.getShopRegion().setText(" " + shops.get(position).getAddress());
        holder.getShopOpening().setText(shops.get(position).getOpening());
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView shopName;
        private TextView shopRegion;
        private TextView shopPhone;
        private TextView shopOpening;

        private OnShopListListener onShopListListener;

        public ViewHolder(View itemView, OnShopListListener onShopListListener) {
            super(itemView);

            shopName = itemView.findViewById(R.id.shop_item_shop_name);
            shopRegion = itemView.findViewById(R.id.shop_item_shop_address);
            shopPhone = itemView.findViewById(R.id.shop_item_shop_phone);
            shopOpening = itemView.findViewById(R.id.shop_item_shop_opening);

            this.onShopListListener = onShopListListener;
            itemView.setOnClickListener(this);
        }

        public TextView getShopName() {
            return shopName;
        }

        public TextView getShopRegion() {
            return shopRegion;
        }

        public TextView getShopPhone() {
            return shopPhone;
        }

        public TextView getShopOpening() {
            return shopOpening;
        }

        @Override
        public void onClick(View v) {
            onShopListListener.onShopClick(getAdapterPosition());
        }
    }

    public interface OnShopListListener {
        void onShopClick(int position);
    }
}
