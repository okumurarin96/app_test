package com.stringee.app.dialog_fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.R.menu;
import com.stringee.app.common.Constant;
import com.stringee.app.common.PrefUtils;
import com.stringee.app.common.Utils;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.PackageName;
import com.stringee.messaging.listeners.CallbackListener;

import java.util.ArrayList;
import java.util.List;

public class PackageNamesFragment extends BaseBottomSheetDialogFragment {
    private RecyclerView rvPackageName;
    private ImageButton btnAdd;
    private TextInputEditText etName;
    private View vAdd;
    private com.stringee.app.adapter.PackageNameAdapter adapter;
    private List<PackageName> packageNames = new ArrayList<>();
    private boolean isAdd = false;
    private CallbackListener<List<PackageName>> listener;

    public void setCallBack(CallbackListener<List<PackageName>> listener) {
        this.listener = listener;
    }

    @androidx.annotation.NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_package_name, container);

        ImageButton btnBack = view.findViewById(id.btn_back);
        btnBack.setOnClickListener(this);
        btnAdd = view.findViewById(id.btn_add);
        btnAdd.setOnClickListener(this);
        ImageButton btnDone = view.findViewById(id.btn_done);
        btnDone.setOnClickListener(this);

        vAdd = view.findViewById(id.v_add);
        etName = view.findViewById(id.et_name);

        packageNames = Utils.getListFromStringJSON(PrefUtils.getInstance(requireContext()).getString(Constant.PREF_PACKAGE_NAME, null), PackageName.class);
        if (Utils.isListEmpty(packageNames)) {
            packageNames = new ArrayList<>();
            packageNames.add(new PackageName(requireActivity().getPackageName()));
        }

        rvPackageName = view.findViewById(id.rv_package_name);
        rvPackageName.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPackageName.setHasFixedSize(true);

        adapter = new com.stringee.app.adapter.PackageNameAdapter(requireActivity(), packageNames);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                PackageName packageName = packageNames.get(position);
                packageName.setSelected(!packageName.isSelected());
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                super.onLongClick(view, position);
                PopupMenu popupMenu = new PopupMenu(requireContext(), view);
                popupMenu.setGravity(Gravity.END);
                popupMenu.getMenuInflater().inflate(menu.menu_delete, popupMenu.getMenu());
                popupMenu.setForceShowIcon(true);
                popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();
                            if (itemId == id.menu_delete) {
                                packageNames.remove(position);
                                adapter.notifyItemRemoved(position);
                                savePackageName();
                                return true;
                            }
                            return false;
                        }
                );
                popupMenu.show();
            }
        });
        rvPackageName.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == id.btn_add) {
            isAdd = true;
            updateView();
        } else if (vId == id.btn_done) {
            if (isAdd) {
                if (Utils.isStringEmpty(etName.getText())) {
                    Utils.reportMessage(requireContext(), "package name cannot empty");
                    return;
                }
                packageNames.add(new PackageName(etName.getText().toString().trim()));
                adapter.notifyItemInserted(packageNames.size() - 1);
                etName.setText("");
                savePackageName();
                isAdd = false;
                updateView();
            } else {
                if (listener != null) {
                    savePackageName();
                    listener.onSuccess(packageNames);
                }
                dismiss();
            }
        } else if (vId == id.btn_back) {
            if (isAdd) {
                etName.setText("");
                isAdd = false;
                updateView();
            } else {
                dismiss();
            }
        }
    }

    private void updateView() {
        vAdd.setVisibility(isAdd ? View.VISIBLE : View.GONE);
        rvPackageName.setVisibility(isAdd ? View.GONE : View.VISIBLE);
        btnAdd.setVisibility(isAdd ? View.GONE : View.VISIBLE);
    }

    private void savePackageName() {
        PrefUtils.getInstance(requireContext()).putString(Constant.PREF_PACKAGE_NAME, Utils.convertObjectToStringJSON(packageNames));
    }
}
