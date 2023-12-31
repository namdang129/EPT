package com.example.ept

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.ept.adapter.LessonByMuscleAdapter
import com.example.ept.adapter.LessonPlanAdapter
import com.example.ept.model.LessonInfo
import com.example.ept.model.UserLessonModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class DashboardFragment : Fragment() {
    private lateinit var rvLessonByPlan: RecyclerView
    private lateinit var rvLessonByMuscleEz: RecyclerView
    private lateinit var rvLessonByMuscleDiff: RecyclerView

    private lateinit var mLessonPlanAdapter: LessonPlanAdapter
    private lateinit var mLessonByMuscleAdapterEz: LessonByMuscleAdapter
    private lateinit var mLessonByMuscleAdapterDiff: LessonByMuscleAdapter

    private lateinit var _lstLessonByPlan: MutableList<LessonInfo>
    private lateinit var _lstLessonByMucleEz: MutableList<LessonInfo>
    private lateinit var _lstLessonByMucleDiff: MutableList<LessonInfo>

    private lateinit var rootView: View
    private lateinit var database: DatabaseReference
    private var _user_id: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)
        database = FirebaseDatabase.getInstance().reference
        _user_id = 1

        LoadData()

        // Inflate the layout for this fragment
        return rootView
    }

    private fun LoadData() {
        rvLessonByPlan = rootView.findViewById(R.id.rvLessonByPlan)
        rvLessonByMuscleEz = rootView.findViewById(R.id.rvLessonMuscle)
        rvLessonByMuscleDiff = rootView.findViewById(R.id.rvLessonMuscleDiff)


        _lstLessonByPlan = ArrayList<LessonInfo>()
        _lstLessonByMucleEz = ArrayList<LessonInfo>()
        _lstLessonByMucleDiff = ArrayList<LessonInfo>()

        mLessonPlanAdapter = LessonPlanAdapter(_lstLessonByPlan)
        rvLessonByPlan.setAdapter(mLessonPlanAdapter)

        mLessonByMuscleAdapterEz = LessonByMuscleAdapter(_lstLessonByMucleEz)
        rvLessonByMuscleEz.setAdapter(mLessonByMuscleAdapterEz)

        mLessonByMuscleAdapterDiff = LessonByMuscleAdapter(_lstLessonByMucleDiff)
        rvLessonByMuscleDiff.setAdapter(mLessonByMuscleAdapterDiff)

        val myRef = database.child("Workout")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //thông tin bài tập của user đăng nhập
                val lstUserLesson: ArrayList<UserLessonModel> = ArrayList<UserLessonModel>()
                for (dataSnapshot in snapshot.child("User_Lesson").children) {
                    val model = dataSnapshot.getValue<UserLessonModel>()
                    if (model != null) {
                        lstUserLesson.add(model)
                    }
                }

                _lstLessonByPlan!!.clear()
                _lstLessonByMucleEz!!.clear()
                _lstLessonByMucleDiff!!.clear()

                for (dataSnapshot in snapshot.child("Lesson").children) {
                    val model = dataSnapshot.getValue<LessonInfo>()
                    if (model != null) {
                        if (model.lesson_Type == 1) {
                            if (lstUserLesson.any { x -> x.user_Id == _user_id && x.lesson_Id == model.lesson_Id }) {
                                _lstLessonByPlan!!.add(model)
                            }
                        } else if (model.lesson_Type == 2) {
                            if (model.level == 1) {
                                _lstLessonByMucleEz!!.add(model)
                            } else if (model.level == 3) {
                                _lstLessonByMucleDiff!!.add(model)
                            }
                        }
                    }
                }

                // Thông báo cho Adapter biết là dữ liệu đã thay đổi
                mLessonPlanAdapter!!.notifyDataSetChanged()
                mLessonByMuscleAdapterEz!!.notifyDataSetChanged()
                mLessonByMuscleAdapterDiff!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
//                Toast.makeText(
//                    this@DashboardFragment,
//                    "Lấy danh sách bài tập thất bại!",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        })


    }
}