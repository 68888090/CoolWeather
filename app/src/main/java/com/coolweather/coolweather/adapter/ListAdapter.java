//package com.coolweather.coolweather.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
//    interface SetOnItemClickListener{
//        void onItemClick(int position);
//    }
//    private List<String> dataList;
//    private SetOnItemClickListener listener;
//    ListAdapter(List<String>dataList,SetOnItemClickListener listener){
//        this.dataList=dataList;
//        this.listener=listener;
//    }
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view= LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String context=dataList.get(position);
//        holder.textView.setText(context);
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener!=null){
//                    listener.onItemClick(position);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return dataList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//        TextView textView;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textView=itemView.findViewById(android.R.id.text1);
//        }
//    }
//}