package com.manga.m2ng2.model

import android.widget.Filter
import com.manga.m2ng2.adapter.TheLoaiAdapter

class FilterTheLoai(private var filterList: ArrayList<TheLoaiModel>, private var adapter: TheLoaiAdapter) :
    Filter() {



    // Lọc kết quả và trả về FilterResults
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        // Kiểm tra rằng constraint không rỗng và khác null
        if (constraint != null && constraint.isNotEmpty()) {
            // Chuyển constraint thành chữ thường để không phân biệt chữ hoa chữ thường
            val constraintLowerCase = constraint.toString().toLowerCase()
            // Tạo một danh sách tạm để lưu kết quả tìm kiếm
            val filteredList = ArrayList<TheLoaiModel>()
            // Lặp qua danh sách gốc và tìm các phần tử khớp với constraint
            if(filterList.size > 0) {
                for (i in 0 until filterList.size) {
                    if (filterList[i].theLoai!!.toLowerCase().contains(constraintLowerCase)) {
                        // Nếu phần tử khớp với constraint thì thêm vào danh sách tạm
                        filteredList.add(filterList[i])
                    }
                }
            }

            results.count = filteredList.size
            results.values = filteredList
        } else {
            // Nếu constraint rỗng hoặc null, trả về danh sách gốc và số lượng phần tử của nó
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    // Cập nhật danh sách được lọc trong Adapter và thông báo cho RecyclerView để cập nhật giao diện người dùng
    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.updateList(results?.values as ArrayList<TheLoaiModel>)
    }
}
