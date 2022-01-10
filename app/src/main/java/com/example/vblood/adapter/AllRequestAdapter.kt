package com.example.vblood.adapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vblood.R
import com.example.vblood.common.MyLocation
import com.example.vblood.model.Request
import com.example.vblood.util.OfflineData
import com.example.vblood.viewmodel.vBloodViewModel
import com.google.android.gms.maps.model.LatLng
import java.lang.IndexOutOfBoundsException
import java.text.DecimalFormat
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class AllRequestAdapter(private val listener:onItemClickListener) : ListAdapter<Request,AllRequestAdapter.RequestViewHolder>(DiffUtil()){

    private var viewModel:vBloodViewModel?=null
    private var showDelButton:Boolean=false

    fun initialize(vBloodViewModel: vBloodViewModel,showDelButton:Boolean){
        viewModel=vBloodViewModel
        this.showDelButton =showDelButton
    }

    inner class RequestViewHolder(view:View) : RecyclerView.ViewHolder(view) , View.OnClickListener{

        private val bloodGroupTv: TextView = view.findViewById(R.id.blood_group_request_ItemView)
        private val messageTv: TextView = view.findViewById(R.id.message_request_ItemView)
        private val timeTv:TextView = view.findViewById(R.id.time_request_ItemView)
        val deleteBtn:ImageButton = view.findViewById(R.id.delete_request_ItemView)
        val distanceTv:TextView = view.findViewById(R.id.distance_request_textView)
        val doneIv:ImageView = view.findViewById(R.id.done_request_ItemView)
        private val isPlasmaTv:TextView = view.findViewById(R.id.plasma_request_TextView)
        var id:String?=null
        fun bind(item:Request){
            bloodGroupTv.text=item.bloodGroup
            messageTv.text=item.requestMessage
            timeTv.text=item.time
            id=item.id
            if(item.isBooked) doneIv.visibility=View.VISIBLE
            if(MyLocation.coords_x!=null && MyLocation.coords_y!=null) {
                val distance = calculationByDistance(
                    LatLng(MyLocation.coords_x!!, MyLocation.coords_y!!),
                    LatLng(item.location_x, item.location_y)
                ).toString() + " Km"
                distanceTv.text = distance
            }
            if(item.isPlasma) isPlasmaTv.visibility=View.VISIBLE
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position!=RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_item_view,parent,false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {

        if(!showDelButton){
            holder.deleteBtn.visibility=View.INVISIBLE
            //older.doneIv.setPadding(0,0,0,10)
            holder.distanceTv.visibility=View.VISIBLE
        }
        holder.deleteBtn.setOnClickListener {
            if(holder.id!=null) {

                val item=getItem(position)
                viewModel!!.addNoOfRequest(item.requestExcept)
                viewModel!!.deleteRequestDataForDonor(holder.id!!,item.bloodGroup)
            }
        }
        val item=getItem(position)
        holder.bind(item)
    }

    class DiffUtil: androidx.recyclerview.widget.DiffUtil.ItemCallback<Request>() {
        override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem.id==newItem.id && oldItem.requestExcept==newItem.requestExcept
        }

        override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
            return oldItem==newItem && oldItem.requestExcept==newItem.requestExcept
        }
    }


    fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371 // radius of earth in Km
        val lat1: Double = StartP.latitude
        val lat2: Double = EndP.latitude
        val lon1: Double = StartP.longitude
        val lon2: Double = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )

        val number2digits:Double = String.format("%.2f", Radius * c).toDouble()
        return (number2digits)
    }

}