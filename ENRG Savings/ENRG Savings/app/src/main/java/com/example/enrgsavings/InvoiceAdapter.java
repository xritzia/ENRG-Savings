package com.example.enrgsavings;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;


/**
 * Adapter class for displaying a list of InvoiceData in a RecyclerView.
 * Manages the layout and interaction of each item in the list, including displaying details
 * and handling popup information dialogs for more detailed invoice data.
 */
public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private final List<InvoiceData> invoiceDataList;
    private Context context;
    private boolean isPopupShowing = false;
    private PopupWindow popupWindow;

    /**
     * Constructs an InvoiceAdapter with a specified list of InvoiceData.
     *
     * @param invoiceDataList The list of InvoiceData to be displayed.
     */
    public InvoiceAdapter(List<InvoiceData> invoiceDataList) {
        this.invoiceDataList = invoiceDataList;
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return Size of the invoiceDataList.
     */
    @Override
    public int getItemCount() {
        return invoiceDataList.size();
    }

    /**
     * Provides a new ViewHolder to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new InvoiceViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_item, parent, false);
        return new InvoiceViewHolder(view);
    }


    /**
     * Updates the contents of the ViewHolder to reflect the item at the given position in the data set.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at
     *                 the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        InvoiceData invoiceData = invoiceDataList.get(position);
        holder.invoiceName.setText(invoiceData.getInvoiceName());
        holder.fixedRate.setText(String.format("%.2f", invoiceData.getFixedRate()));
        holder.finalPrice.setText(String.format("%.2f", invoiceData.getFinalPrice()));
        HashMap<String, Integer> providerDrawableMap = new HashMap<>();
        // Adds the corresponding drawable.
        providerDrawableMap.put("ΔΕΗ", R.drawable.deh);
        providerDrawableMap.put("ELPEDISON", R.drawable.elpedison);
        providerDrawableMap.put("NRG", R.drawable.nrg);
        providerDrawableMap.put("OTE ESTATE", R.drawable.ote);
        providerDrawableMap.put("PROTERGIA", R.drawable.protergia);
        providerDrawableMap.put("VOLTERRA", R.drawable.volterra);
        providerDrawableMap.put("VOLTON", R.drawable.volton);
        providerDrawableMap.put("WE ENERGY", R.drawable.weenergy);
        providerDrawableMap.put("ΕΛΙΝΟΙΛ", R.drawable.elin);
        providerDrawableMap.put("ΖΕΝΙΘ", R.drawable.zenith);
        providerDrawableMap.put("ΗΡΩΝ", R.drawable.hron);
        providerDrawableMap.put("ΦΥΣΙΚΟ ΑΕΡΙΟ ΕΛΛΗΝΙΚΗ ΕΤΑΙΡΙΑ ΕΝΕΡΓΕΙΑΣ", R.drawable.fysikoaerio);
        providerDrawableMap.put("SOLAR ENERGY", R.drawable.solarenergy);
        providerDrawableMap.put("EUNICE", R.drawable.eunice);

        String providerName = invoiceData.getProvider();
        int drawableResourceId = providerDrawableMap.containsKey(providerName) ?
                providerDrawableMap.get(providerName) :
                R.drawable.electricity;
        holder.imageView.setImageResource(drawableResourceId);
        String colorClass = invoiceData.getcolorClass();

        // Displays the corresponding color.
        if (colorClass != null && !colorClass.isEmpty()) {
            switch (colorClass) {
                case "color_green":
                    holder.invoiceName.setBackgroundResource(R.drawable.background_green);
                    break;
                case "color_yellow":
                    holder.invoiceName.setBackgroundResource(R.drawable.background_yellow);
                    break;
                case "color_blue":
                    holder.invoiceName.setBackgroundResource(R.drawable.background_blue);
                    break;
                default:
                    holder.invoiceName.setBackgroundResource(R.drawable.background_default);
                    break;
            }
        } else {
            holder.invoiceName.setBackgroundResource(R.drawable.background_default);
        }
        holder.infoButton.setOnClickListener(v -> showPopupWindow(holder.itemView.getContext(), invoiceData));
    }

    /**
     * Displays a popup window with additional details about an invoice when called.
     *
     * @param context     The UI context in which the popup will be displayed.
     * @param invoiceData The InvoiceData object containing the information to display.
     */
    private void showPopupWindow(Context context, InvoiceData invoiceData) {
        isPopupShowing = true;
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_info, null);
        TextView textViewFixedDiscount = popupView.findViewById(R.id.textview_fixed_discount_title);
        TextView textViewFixedDiscountInf = popupView.findViewById(R.id.textview_fixed_discount_info);
        TextView textViewFinalDiscount = popupView.findViewById(R.id.textview_final_discount_title);
        TextView textViewFinalDiscountInf = popupView.findViewById(R.id.textview_final_discount_info);
        TextView textViewComments = popupView.findViewById(R.id.textview_comments_title);
        TextView textViewCommentsInf = popupView.findViewById(R.id.textview_comments_info);
        Button buttonClosePopup = popupView.findViewById(R.id.button_close_popup);
        TextView textViewNoInfo = popupView.findViewById(R.id.textview_no_info_text);

        if (invoiceData.getFixedDiscount() != null && !invoiceData.getFixedDiscount().trim().isEmpty() && !invoiceData.getFixedDiscount().trim().equals("0")) {
            textViewFixedDiscountInf.setText(invoiceData.getFixedDiscount());
        } else {
            textViewFixedDiscount.setVisibility(View.GONE);
        }

        if (invoiceData.getFinalDiscount() != null && !invoiceData.getFinalDiscount().trim().isEmpty() && !invoiceData.getFinalDiscount().trim().equals("0")) {
            textViewFinalDiscountInf.setText(invoiceData.getFinalDiscount());
        } else {
            textViewFinalDiscount.setVisibility(View.GONE);
        }

        if (invoiceData.getComments() != null && !invoiceData.getComments().trim().isEmpty()) {
            textViewCommentsInf.setText(invoiceData.getComments());
        } else {
            textViewComments.setVisibility(View.GONE);
        }

        if (textViewFixedDiscount.getVisibility() == View.GONE &&
                textViewFinalDiscount.getVisibility() == View.GONE &&
                textViewComments.getVisibility() == View.GONE) {
            textViewNoInfo.setVisibility(View.VISIBLE);
        } else {
            textViewNoInfo.setVisibility(View.GONE);
        }

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        buttonClosePopup.setOnClickListener(v -> dismissPopupWindow());

    }

    /**
     * Dismisses the currently displayed popup window if it is showing.
     */
    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            isPopupShowing = false;
            enableInvoiceActivity();
        }
    }

    /**
     * Enables user interaction with the InvoiceActivity when the popup is not showing.
     */
    private void enableInvoiceActivity() {
        if (context instanceof InvoiceActivity) {
            ((InvoiceActivity) context).setEnabled(!isPopupShowing);
        }
    }

    /**
     * ViewHolder class for the RecyclerView. Holds views that will display InvoiceData items.
     */
    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView invoiceName;
        TextView finalPrice;
        TextView fixedRate;
        Button infoButton;

        /**
         * Constructs a new InvoiceViewHolder.
         *
         * @param itemView The View that will display the InvoiceData.
         */
        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialization of the views within the itemView
            imageView = itemView.findViewById(R.id.image_company_logo);
            invoiceName = itemView.findViewById(R.id.textview_invoice_name);
            fixedRate = itemView.findViewById(R.id.textview_fixed_rate_price);
            finalPrice = itemView.findViewById(R.id.textview_final_price);
            infoButton = itemView.findViewById(R.id.button_info);
        }
    }

}