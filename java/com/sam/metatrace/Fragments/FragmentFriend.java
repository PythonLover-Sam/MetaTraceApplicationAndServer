package com.sam.metatrace.Fragments;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sam.metatrace.Abstract.FragmentBase;
import com.sam.metatrace.Adapter.FragmentFriendListAdapter;
import com.sam.metatrace.Entity.FriendListViewItemBean;
import com.sam.metatrace.Enums.AndroidMessageEnum;
import com.sam.metatrace.MainPage;
import com.sam.metatrace.R;
import com.sam.metatrace.Services.HttpClient;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriend extends FragmentBase implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private View root;
    private ImageButton btn_add_friend;
    private ListView lv;

    private static FragmentFriendListAdapter fragmentFriendListAdapter;

    public static FragmentFriendListAdapter getFragmentFriendListAdapter(){return fragmentFriendListAdapter;}

    private FriendListViewItemBean listViewItemBean;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return root = inflater.inflate(R.layout.fragment_friend, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(fragmentFriendListAdapter == null){
            fragmentFriendListAdapter = new FragmentFriendListAdapter(root.getContext());
        }

        HttpClient.sendPullFriendsListMessage();
        btn_add_friend = root.findViewById(R.id.btn_add_friend);
        btn_add_friend.setOnClickListener(v ->{
            // ??????mainPage?????????????????????fragment
            Message message = new Message();
            message.what = AndroidMessageEnum.GO_TO_ADD_FRIEND_FRAGMENT.getType();
            mHandler.sendMessage(message);
        });
        lv = root.findViewById(R.id.lv_friend_list);

        lv.setAdapter(fragmentFriendListAdapter);
        lv.setOnItemClickListener(this);
        lv.setOnItemLongClickListener(this);

        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if(listViewItemBean != null){
                    if(listViewItemBean.isAccepted){
                        menu.setHeaderTitle("????????????"+listViewItemBean.who +"?????????");
                        menu.add(1, 0, 0, "????????????");
                    }else {
                        menu.setHeaderTitle(listViewItemBean.who + "?????????????????????");
                        menu.add(1, 1, 0, "????????????");

                    }
                }

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!fragmentFriendListAdapter.getData().get(position).isAccepted) {
            // ????????????????????????????????????
            Toast.makeText(root.getContext(), "????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        // ??????mainPage???????????????fragment
        Message message = new Message();
        message.what = AndroidMessageEnum.GO_TO_ChatRoom_FRAGMENT.getType();
        message.obj = fragmentFriendListAdapter.getData().get(position).who;
        mHandler.sendMessage(message);
        ChatWithOneFriendFragment.intentUsername = (String)message.obj;

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // ??????context menu ???groupid?????????fragment?????????groupid??????????????????????????????android????????????????????????
        if(item.getGroupId() != 1) return super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int)info.id;
        String hisUsername = fragmentFriendListAdapter.getData().get(id).who;

        switch (item.getItemId()) {
            case 0:
                // ??????????????????
                HttpClient.sendAcceptOrDenyFriendRequest(hisUsername, "deny");
                fragmentFriendListAdapter.getData().remove(id);
                fragmentFriendListAdapter.notifyDataSetChanged();
                Toast.makeText(root.getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                // ????????????
                HttpClient.sendAcceptOrDenyFriendRequest(hisUsername, "accept");
                fragmentFriendListAdapter.getData().get(id).isAccepted=true;
                fragmentFriendListAdapter.getData().get(id).icon=R.drawable.icon_smile_face;
                fragmentFriendListAdapter.notifyDataSetChanged();
                Toast.makeText(root.getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                break;
            default:
                return true;
        }
        return true;
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        listViewItemBean = fragmentFriendListAdapter.getData().get(position);
        return false;
    }
}
