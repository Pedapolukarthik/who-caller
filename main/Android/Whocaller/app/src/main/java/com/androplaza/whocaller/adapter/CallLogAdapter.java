/*
 * Company : AndroPlaza
 * Detailed : Software Development Company in Sri Lanka
 * Developer : Buddhika
 * Contact : support@androplaza.store
 * Whatsapp : +94711920144
 */

package com.androplaza.whocaller.adapter;

import static com.androplaza.whocaller.utils.Utils.formatDate;
import static com.androplaza.whocaller.utils.Utils.generateAvatar;
import static com.androplaza.whocaller.utils.Utils.getCallTypeString;
import static com.androplaza.whocaller.utils.Utils.getContactImage;
import static com.androplaza.whocaller.utils.Utils.isValidName;
import static com.androplaza.whocaller.utils.Utils.toTextCase;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.androplaza.whocaller.Config;
import com.androplaza.whocaller.R;
import com.androplaza.whocaller.activities.ContactDetailsActivity;
import com.androplaza.whocaller.database.sqlite.BlockCallerDbHelper;
import com.androplaza.whocaller.database.sqlite.ContactsDataDb;
import com.androplaza.whocaller.modal.CallLogItem;
import com.androplaza.whocaller.modal.Contact;
import com.androplaza.whocaller.modal.UserProfile;
import com.androplaza.whocaller.utils.Utils;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CallLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CallLogItem> callLogItems;
    public List<CallLogItem> callLogItemsFull;
    public Set<Integer> selectedItems = new HashSet<>();
    public boolean multiSelectMode = false;
    private SelectionListener selectionListener;
    private SetDefault setDefault;
    public Context context;
    public boolean isExpandable;
    public boolean isCallLogLay;
    private static final int MAX_ITEMS_COLLAPSED = 3;

    String displayName;
    boolean isSpam = false;
    boolean isContact = true;
    ContactsDataDb dbContactsHelper;
    BlockCallerDbHelper dbCallBlockHelper;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private boolean isDefaultDialer;

    public CallLogAdapter(List<CallLogItem> callLogItems, Context context, boolean isExpandable, boolean isCallLogLay) {
        this.callLogItems = callLogItems != null ? callLogItems : new ArrayList<>();
        this.callLogItemsFull = new ArrayList<>(this.callLogItems);
        this.context = context;
        this.isExpandable = isExpandable;
        this.isCallLogLay = isCallLogLay;
        this.dbContactsHelper = new ContactsDataDb(context);
        this.dbCallBlockHelper = new BlockCallerDbHelper(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setShowHeader(boolean isDefaultDialer) {
        this.isDefaultDialer = isDefaultDialer;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (!isDefaultDialer && position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void updateCallLogs(List<CallLogItem> callLogItems) {
        this.callLogItems = callLogItems;
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void setSetDefault(SetDefault setDefault) {
        this.setDefault = setDefault;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_set_default, parent, false);
            return new CallLogAdapter.HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(isCallLogLay ? R.layout.item_call_log : R.layout.item_call_log2, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            CallLogAdapter.HeaderViewHolder headerHolder = (CallLogAdapter.HeaderViewHolder) holder;
            headerHolder.setDefault.setOnClickListener(v -> setDefault.onItemClick(true));

        } else {
            int adjustedPosition = !isDefaultDialer ? position - 1 : position;
            ViewHolder callLogtHolder = (ViewHolder) holder;
            CallLogItem item = callLogItems.get(adjustedPosition);

            String newPhoneNumber = item.getPhoneNumber();

            callLogtHolder.whoProfile.setVisibility(View.GONE);
            callLogtHolder.whoTag.setVisibility(View.GONE);
            callLogtHolder.name.setTextColor(context.getResources().getColor(R.color.title_color, null));
            callLogtHolder.callType.setTextColor(ContextCompat.getColor(context, R.color.sub_title_color));
            callLogtHolder.callDate.setTextColor(context.getResources().getColor(R.color.sub_title_color, null));
            isSpam = false;
            isContact = true;

            if (item.getName() != null) {
                if (isValidName(item.getName())) {
                    displayName = item.getName();
                    callLogtHolder.name.setText(toTextCase(displayName));
                    isContact = true;
                } else {
                    displayName = Config.UNKNOWN_CALLER_NAME;
                    callLogtHolder.name.setText(item.getPhoneNumber());
                    isContact = false;
                }
            } else {
                displayName = Config.UNKNOWN_CALLER_NAME;
                callLogtHolder.name.setText(item.getPhoneNumber());
                isContact = false;
            }



            Bitmap contactImage = getContactImage(context, item.getPhoneNumber());
            if (contactImage != null) {
                callLogtHolder.avatar.setImageBitmap(contactImage);
            } else {
                callLogtHolder.avatar.setImageBitmap(generateAvatar(isValidName(displayName) ? displayName : "U"));
            }


            callLogtHolder.callType.setText(getCallTypeString(item.getCallType()));
            callLogtHolder.callTypeIcon.setImageResource(item.getCallTypeIconResId());
            callLogtHolder.callDate.setText(formatDate(item.getCallDate()));
            callLogtHolder.callDuration.setText(item.getFormattedCallDuration());

            Contact contactdb = dbContactsHelper.getContactByPhoneNumber(newPhoneNumber);

            if (!isContact) {
                if (contactdb != null) {
                    if (contactdb.getContactsBy() != null) {
                        if (contactdb.getContactsBy().equals("whocaller") || displayName.equals(Config.UNKNOWN_CALLER_NAME)) {
                            callLogtHolder.whoTag.setVisibility(View.VISIBLE);
                        }
                    }
                    if (!Utils.isPhoneNumberSaved(newPhoneNumber, context) && isValidName(contactdb.getName())) {
                        callLogtHolder.whoTag.setVisibility(View.VISIBLE);
                    }

                    if (isValidName(contactdb.getName())) {
                        displayName = contactdb.getName();
                        callLogtHolder.name.setText(toTextCase(contactdb.getName()));
                        callLogtHolder.avatar.setImageBitmap(generateAvatar(isValidName(contactdb.getName()) ? contactdb.getName() : "U"));

                    }

                    if (contactdb.isWho()) {
                        callLogtHolder.whoProfile.setVisibility(View.VISIBLE);

                    }

                    if (contactdb.isSpam()) {
                        isSpam = true;
                        callLogtHolder.callType.setTextColor(ContextCompat.getColor(context, R.color.red));
                        callLogtHolder.callTypeIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                        callLogtHolder.callDate.setTextColor(context.getResources().getColor(R.color.red, null));
                        if (isCallLogLay) {
                            callLogtHolder.callType.setText("Spammer");
                            callLogtHolder.avatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.spam_circle));
                        } else {
                            callLogtHolder.callType.setText("Spammer - " + Utils.getCallTypeString(item.getCallType()));
                        }
                    }
                }
            }

            if (contactdb != null) {
                if (contactdb.isSpam()) {
                    isSpam = true;
                    callLogtHolder.callType.setTextColor(ContextCompat.getColor(context, R.color.red));
                    callLogtHolder.callTypeIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                    callLogtHolder.callDate.setTextColor(context.getResources().getColor(R.color.red, null));
                    if (isCallLogLay) {
                        callLogtHolder.callType.setText("Spammer");
                        callLogtHolder.avatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.spam_circle));
                    } else {
                        callLogtHolder.callType.setText("Spammer - " + Utils.getCallTypeString(item.getCallType()));
                    }
                }
            }

            if (!isContact) {
                if (Utils.isNetworkAvailable(context)) {
                    ((ViewHolder) holder).profile_progress.setVisibility(View.VISIBLE);
                    if (displayName.equals("Unknown") || contactdb == null || callLogtHolder.whoTag.getVisibility() == View.VISIBLE || callLogtHolder.whoProfile.getVisibility() == View.VISIBLE) {
                            String finalNewPhoneNumber = newPhoneNumber;
                            Utils.getContactDataDetails(newPhoneNumber, new Utils.ContactDataCallback() {
                                @Override
                                public void onSuccess(Object data) {
                                    if (data != null) {
                                        if (data instanceof UserProfile) {
                                            UserProfile profile = (UserProfile) data;
                                            displayName = profile.getFirstName() + " " + profile.getLastName();
                                            callLogtHolder.whoProfile.setVisibility(View.VISIBLE);
                                            callLogtHolder.name.setText(toTextCase(displayName));
                                            callLogtHolder.whoTag.setVisibility(View.VISIBLE);

                                            callLogtHolder.avatar.setImageBitmap(generateAvatar(isValidName(displayName) ? displayName : "U"));
                                            dbContactsHelper.addContactOrUpdate(displayName, finalNewPhoneNumber, true, false, "", "", null, null, "whocaller");
                                            ((ViewHolder) holder).profile_progress.setVisibility(View.GONE);
                                        } else if (data instanceof Contact) {
                                            Contact contact = (Contact) data;
                                            contact.setIsWho(false);
                                            contact.setContactsBy("whocaller");
                                            displayName = contact.getName();
                                            callLogtHolder.name.setText(toTextCase(displayName));
                                            callLogtHolder.whoTag.setVisibility(View.VISIBLE);
                                            callLogtHolder.avatar.setImageBitmap(generateAvatar(isValidName(displayName) ? displayName : "U"));
                                            if (contact.isSpam()) {
                                                isSpam = true;
                                                callLogtHolder.name.setTextColor(context.getResources().getColor(R.color.red, null));
                                                callLogtHolder.avatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.spam_circle));
                                            }else {
                                                isSpam = false;
                                            }
                                            dbContactsHelper.addContactOrUpdate(contact);
                                            ((ViewHolder) holder).profile_progress.setVisibility(View.GONE);

                                        } else {
                                            Log.e("ContactDataCallback", "Error: Unknown data type");
                                            ((ViewHolder) holder).profile_progress.setVisibility(View.GONE);
                                        }
                                    } else {
                                        Log.e("ContactDataCallback", "Error: Received null data");
                                        ((ViewHolder) holder).profile_progress.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Log.e("ContactDataCallback", "Error: " + errorMessage);
                                    ((ViewHolder) holder).profile_progress.setVisibility(View.GONE);
                                }
                            });


                    }
                }
            }

            callLogtHolder.phoneNumber.setText(newPhoneNumber);


            callLogtHolder.getCall.setVisibility(View.VISIBLE);

            String finalNewPhoneNumber = newPhoneNumber;
            callLogtHolder.getCall.setOnClickListener(v -> Utils.makeCall(finalNewPhoneNumber, context));

            if (isCallLogLay) {
                if (dbCallBlockHelper.isPhoneNumberBlocked(newPhoneNumber)) {
                    callLogtHolder.callType.setTextColor(ContextCompat.getColor(context, R.color.red));
                    callLogtHolder.callType.setText("Blocked");
                    callLogtHolder.callTypeIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                    callLogtHolder.callDate.setTextColor(context.getResources().getColor(R.color.red, null));
                    callLogtHolder.avatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.block_circle));
                }

                callLogtHolder.itemView.setOnClickListener(v -> {
                    if (multiSelectMode) {
                        toggleSelection(position);
                    } else {
                        Intent intent = new Intent(v.getContext(), ContactDetailsActivity.class);
                        intent.putExtra("name", callLogtHolder.name.getText().toString());
                        intent.putExtra("phoneNumber", finalNewPhoneNumber);
                        intent.putExtra("callType", getCallTypeString(item.getCallType()));
                        intent.putExtra("callDate", formatDate(item.getCallDate()));
                        intent.putExtra("isBlock", dbCallBlockHelper.isPhoneNumberBlocked(finalNewPhoneNumber));
                        intent.putExtra("isSpam", isSpam);


                        if (callLogtHolder.whoTag.getVisibility() == View.VISIBLE || callLogtHolder.whoProfile.getVisibility() == View.VISIBLE) {
                            intent.putExtra("whocaller", "whocaller");
                        }
                        if (callLogtHolder.whoProfile.getVisibility() == View.VISIBLE) {
                            intent.putExtra("whoprofile", true);

                        }

                        v.getContext().startActivity(intent);
                    }
                });

                callLogtHolder.itemView.setOnLongClickListener(v -> {
                    if (!multiSelectMode) {
                        multiSelectMode = true;
                        selectionListener.onSelectionModeChange(true);
                    }

                    toggleSelection(position);
                    return true;

                });
            } else {
                callLogtHolder.name.setVisibility(View.GONE);
                callLogtHolder.avatar.setVisibility(View.GONE);
                if (dbCallBlockHelper.isPhoneNumberBlocked(newPhoneNumber)) {
                    callLogtHolder.callType.setTextColor(ContextCompat.getColor(context, R.color.red));
                    callLogtHolder.callTypeIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red)));
                    callLogtHolder.callDate.setTextColor(context.getResources().getColor(R.color.red, null));
                    callLogtHolder.callType.setText("Blocked - " + getCallTypeString(item.getCallType()));

                }
            }


            if (multiSelectMode) {
                int selectedColor = ContextCompat.getColor(context, R.color.colorPrimary_low);
                int defaultColor = Color.TRANSPARENT;
                holder.itemView.setBackgroundColor(selectedItems.contains(position) ? selectedColor : defaultColor);
                Bitmap avatarBitmap = generateAvatar(displayName != null ? displayName : "U");
                Drawable avatarDrawable = new BitmapDrawable(context.getResources(), avatarBitmap);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.mark_circle);
                callLogtHolder.avatar.setImageDrawable(selectedItems.contains(position) ? drawable : avatarDrawable);

            }
        }

    }

    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }

        notifyItemChanged(position);
        selectionListener.onItemSelectionChange(selectedItems.size());
    }

    @Override
    public int getItemCount() {
        boolean isExpanded = false;
        //return isExpandable && !isExpanded ? Math.min(callLogItems.size()  + 1, MAX_ITEMS_COLLAPSED) : callLogItems.size() + 1;

        if (!isDefaultDialer) {
            if (isExpandable && !isExpanded) {
                return Math.min(callLogItems.size() + 1, MAX_ITEMS_COLLAPSED) + 1;
            } else {
                return callLogItems.size() + 1;
            }
        } else {
            if (isExpandable && !isExpanded) {
                return Math.min(callLogItems.size(), MAX_ITEMS_COLLAPSED);
            } else {
                return callLogItems.size();
            }
        }

    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView setDefault;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            setDefault = itemView.findViewById(R.id.set_default);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deselectAll() {
        selectedItems.clear();
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onItemSelectionChange(selectedItems.size());
        }
    }


    public boolean isAllSelected() {
        return getItemCount() == selectedItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll() {
        selectedItems.clear();
        for (int i = 0; i < callLogItems.size(); i++) {
            selectedItems.add(i);
        }
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onItemSelectionChange(selectedItems.size());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
        if (selectionListener != null) {
            selectionListener.onItemSelectionChange(selectedItems.size());
        }
    }

    public void deleteSelectedItems() {
        List<CallLogItem> itemsToRemove = new ArrayList<>();

        for (int position : selectedItems) {
            CallLogItem item = callLogItems.get(position);
            itemsToRemove.add(item);
        }

        deleteCallLogEntries(itemsToRemove);

        callLogItems.removeAll(itemsToRemove);
        callLogItemsFull.removeAll(itemsToRemove);
        clearSelection();
        selectionListener.onItemDeleteChange(true);
    }

    private void deleteCallLogEntries(List<CallLogItem> items) {
        ContentResolver resolver = context.getContentResolver();
        for (CallLogItem item : items) {
            try {
                String phoneNumber = item.getPhoneNumber();
                String callDate = item.getCallDate();
                Uri callLogUri = CallLog.Calls.CONTENT_URI;
                String selection = CallLog.Calls.NUMBER + " = ? AND " + CallLog.Calls.DATE + " = ?";
                String[] selectionArgs = {phoneNumber, callDate};

                resolver.delete(callLogUri, selection, selectionArgs);

            } catch (SecurityException e) {
                Log.e("CallLogAdapter", "SecurityException: " + e.getMessage());
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phoneNumber, callType, callDate, callDuration;
        public ImageView callTypeIcon, whoTag, whoProfile;
        public RoundedImageView avatar;
        public ConstraintLayout getCall;

        public CircularProgressIndicator profile_progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phoneNumber = itemView.findViewById(R.id.phone_number);
            callType = itemView.findViewById(R.id.call_type);
            callDate = itemView.findViewById(R.id.call_date);
            callTypeIcon = itemView.findViewById(R.id.call_type_icon);
            avatar = itemView.findViewById(R.id.avatar);
            callDuration = itemView.findViewById(R.id.call_duration);
            whoTag = itemView.findViewById(R.id.who_tag);
            whoProfile = itemView.findViewById(R.id.who_profile);
            getCall = itemView.findViewById(R.id.get_call);
            profile_progress = itemView.findViewById(R.id.profile_progress);


        }
    }

    public interface SelectionListener {
        void onSelectionModeChange(boolean enabled);

        void onItemSelectionChange(int count);

        void onItemDeleteChange(boolean deleted);

    }

    public interface SetDefault {
        void onItemClick(boolean click);
    }
}
