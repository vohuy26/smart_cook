package com.example.smart_cook.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smart_cook.R
import com.example.smart_cook.model.ChatMessage

class ChatAdapter(
    private val chatList: MutableList<ChatMessage>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        const val USER = 1

        const val BOT = 2
    }

    override fun getItemViewType(
        position: Int
    ): Int {

        return if (
            chatList[position].isUser
        ) {
            USER
        } else {
            BOT
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return if (viewType == USER) {

            val view = LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_chat_user,
                parent,
                false
            )

            UserViewHolder(view)

        } else {

            val view = LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_chat_bot,
                parent,
                false
            )

            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        val message =
            chatList[position]

        if (holder is UserViewHolder) {

            holder.txtMessage.text =
                message.message

            holder.txtTime.text =
                message.time

        } else if (holder is BotViewHolder) {

            holder.txtMessage.text =
                message.message

            holder.txtTime.text =
                message.time
        }
    }

    override fun getItemCount(): Int {

        return chatList.size
    }

    class UserViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtMessage: TextView =
            itemView.findViewById(
                R.id.txtUserMessage
            )

        val txtTime: TextView =
            itemView.findViewById(
                R.id.txtUserTime
            )
    }

    class BotViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val txtMessage: TextView =
            itemView.findViewById(
                R.id.txtBotMessage
            )

        val txtTime: TextView =
            itemView.findViewById(
                R.id.txtBotTime
            )
    }
}