package com.xborg.vendx.activities.feedbackActivity.fragments.feedbackForm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.xborg.vendx.R
import com.xborg.vendx.activities.feedbackActivity.SharedViewModel
import com.xborg.vendx.database.Feedback
import kotlinx.android.synthetic.main.fragment_feedback_form.*

class FeedbackFormFragment : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel = ViewModelProviders.of(activity!!).get(SharedViewModel::class.java)

        submit_button.setOnClickListener {
            //TODO: check if form is fully filled
            val body = feedback_body_edit_text.text.toString()
            val topic = feedback_topic_spinner.selectedItem.toString()
            if(body != "") {
                sharedViewModel.userFeedback.value = Feedback(body = body, topic = topic)
                sharedViewModel.postFeedback()
            }
        }
    }
}
