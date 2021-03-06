
package com.essential.usdriving.ui.exam_simulator;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.essential.usdriving.R;
import com.essential.usdriving.app.BaseFragment;
import com.essential.usdriving.database.DataSource;
import com.essential.usdriving.entity.BaseEntity;
import com.essential.usdriving.entity.Question;
import com.essential.usdriving.ui.home.HomeFragment;
import com.essential.usdriving.ui.widget.AnswerChoicesItem;
import com.essential.usdriving.ui.widget.QuestionDialogFragment;
import com.essential.usdriving.ui.widget.QuestionNoItemWrapper;
import com.essential.usdriving.ui.widget.WarningDialog;
import com.essential.usdriving.ui.written_test.DMVWrittenTestFragment;
import com.essential.usdriving.ui.written_test.OnQuestionPagerClickListener;

import java.util.ArrayList;

/**
 * Created by the_e_000 on 8/14/2015.
 */
public class ExamSimulatorDoExamFragment extends BaseFragment implements ViewPager.OnPageChangeListener, QuestionNoItemWrapper.OnItemQuestionClickListener, View.OnClickListener, WarningDialog.OnDialogItemClickListener, OnQuestionPagerClickListener {

    private HorizontalScrollView horizontalScrollView;
    private LinearLayout layoutScrollContent;
    private ViewPager viewPager;

    private TextView minute1;
    private TextView minute2;

    private TextView second1;
    private TextView second2;

    private ArrayList<Question> questions;
    private ArrayList<AnswerChoicesItem> answerChoices;
    private ArrayList<QuestionNoItemWrapper> listItemQues;
    private int currentQuesIndex;
    private QuestionPagerAdapter adapter;
    private CountDownTimer timer;
    private int min1, min2, sec1, sec2;
    private WarningDialog dialog, warningDialog;
    private final static int INTERVAL = 1000, LIMIT_TIME = 3600000;
    private MenuItem menuToolbarResult;
    private int timeLeft;

    @Override
    protected boolean enableBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResIdContentView() {
        return R.layout.fragment_exam_simulator_do_exam;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeLeft = 0;
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        warningDialog = new WarningDialog(getActivity(), 3);
        warningDialog.setOnDialogItemClickListener(new WarningDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(int code) {
                if (code == WarningDialog.OK) {
                    warningDialog.dismiss();
                    timer.cancel();
                    getFragmentManager().popBackStack(HomeFragment.HOME_TRANSACTION, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    timer.start();
                    warningDialog.dismiss();
                }
            }
        });
        warningDialog.show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.Result).setVisible(true);
        menuToolbarResult = menu.findItem(R.id.Result);
        menuToolbarResult.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                timer.cancel();
                dialog = new WarningDialog(getActivity(), 1);
                dialog.setOnDialogItemClickListener(new WarningDialog.OnDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(int code) {
                        if (code == WarningDialog.OK) {
                            dialog.dismiss();
                            ExamSimulatorTestResultFragment fragment = new ExamSimulatorTestResultFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("Questions", questions);
                            timer.cancel();
                            fragment.setArguments(bundle);
                            replaceFragment(fragment, getString(R.string.title_exam_simulator));
                        } else {
                            timer.start();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
        super.onPrepareOptionsMenu(menu);


    }

    @Override
    protected void onCreateContentView(View rootView, Bundle savedInstanceState) {
        currentQuesIndex = 0;
        findViews(rootView);
        min1 = 6;
        min2 = 0;
        sec1 = 0;
        sec2 = 0;

        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadData();
        addQuestionList();
        adapter = new QuestionPagerAdapter(getActivity(), questions);
        adapter.setOnQuestionPagerClickListener(this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Questions", questions);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected String setTitle() {
        return getString(R.string.title_exam_simulator);
    }


    @Override
    protected boolean enableToolbar() {
        return true;
    }

    @Override
    public void defineButtonResult() {

    }


    @Override
    public void onItemQuestionClick(QuestionNoItemWrapper item) {
        setAllQuesNoItemInActive();
        item.setActive(true);
        int index = listItemQues.indexOf(item);
        scrollToCenter(item);
        viewPager.setCurrentItem(index, true);
        currentQuesIndex = index;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setAllQuesNoItemInActive();
        listItemQues.get(position).setActive(true);
        scrollToCenter(listItemQues.get(position));
        currentQuesIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDialogItemClick(int code) {
        if (code == WarningDialog.OK) {
            dialog.dismiss();
            ExamSimulatorTestResultFragment fragment = new ExamSimulatorTestResultFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("Questions", questions);
            timer.cancel();
            fragment.setArguments(bundle);
            replaceFragment(fragment, getString(R.string.title_exam_simulator));
        } else {
            startTimer();
            dialog.dismiss();
        }
    }

    private void findViews(View rootView) {
        horizontalScrollView = (HorizontalScrollView) rootView.findViewById(R.id.horizontalScrollView);
        layoutScrollContent = (LinearLayout) rootView.findViewById(R.id.layoutScrollContent);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        minute1 = (TextView) rootView.findViewById(R.id.minute_1);
        minute2 = (TextView) rootView.findViewById(R.id.minute_2);

        second1 = (TextView) rootView.findViewById(R.id.second_1);
        second2 = (TextView) rootView.findViewById(R.id.second_2);


    }

    private void addQuestionList() {
        for (int i = 0; i < questions.size(); i++) {
            QuestionNoItemWrapper questionNo = new QuestionNoItemWrapper(getActivity());
            questionNo.setText("" + questions.get(i).questionNo);
            layoutScrollContent.addView(questionNo.getView());
            if (i == 0) {
                questionNo.setActive(true);
            }
            if (questions.get(i).myAnswer != BaseEntity.ANSWER_NOT_CHOOSE) {
                questionNo.setHighlight();
            }
            questionNo.setOnItemQuestionClickListener(this);
            listItemQues.add(questionNo);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    private void setAllQuesNoItemInActive() {
        for (QuestionNoItemWrapper item : listItemQues) {
            item.setActive(false);
        }
    }

    private void scrollToCenter(QuestionNoItemWrapper questionNoItemWrapper) {
        int centerX = horizontalScrollView.getWidth() / 2;
        int[] itemPos = new int[]{0, 0};
        questionNoItemWrapper.getView().getLocationOnScreen(itemPos);
        int x = itemPos[0];
        int offset = x - centerX + questionNoItemWrapper.getView().getWidth() / 2;
        horizontalScrollView.smoothScrollTo(horizontalScrollView.getScrollX() + offset, 0);
    }

    private void loadData() {
        if (questions == null) {
            questions = DataSource.getInstance().getQuestions();
        } else {
            for (int i = 0; i < questions.size(); i++) {
                questions.get(i).myAnswer = BaseEntity.ANSWER_NOT_CHOOSE;
            }
        }
        listItemQues = new ArrayList<>();
        answerChoices = new ArrayList<>();
        currentQuesIndex = 0;
    }

    private void makeTime() {
        sec2--;
        if (sec2 == -1) {
            sec2 = 9;
            sec1--;
            if (sec1 == -1) {
                sec1 = 5;
                min2--;
                if (min2 == -1) {
                    min2 = 9;
                    min1--;
                }
            }
        }
        minute1.setText("" + min1);
        minute2.setText("" + min2);
        second1.setText("" + sec1);
        second2.setText("" + sec2);
        timeLeft++;
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    private void startTimer() {
        timer = new CountDownTimer(LIMIT_TIME - INTERVAL * timeLeft, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                makeTime();
            }

            @Override
            public void onFinish() {
                ExamSimulatorTestResultFragment fragment = new ExamSimulatorTestResultFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("Questions", questions);
                fragment.setArguments(bundle);
                replaceFragment(fragment, getString(R.string.title_exam_simulator));
            }
        };
        timer.start();
    }

    @Override
    public void OnPagerItemClick(AnswerChoicesItem item) {
        if (questions.get(currentQuesIndex).myAnswer == DataSource.ANSWER_NOT_CHOSEN) {
            questions.get(currentQuesIndex).myAnswer = item.getIndex();
            QuestionNoItemWrapper wrapper = listItemQues.get(currentQuesIndex);
            if (!wrapper.isHighlight) {
                wrapper.setHighlight();
                horizontalScrollView.invalidate();
            }
            if (currentQuesIndex < 29) {
                currentQuesIndex++;
                viewPager.setCurrentItem(currentQuesIndex, true);
                scrollToCenter(listItemQues.get(currentQuesIndex));
            }

        } else {
            questions.get(currentQuesIndex).myAnswer = item.getIndex();
            QuestionNoItemWrapper wrapper = listItemQues.get(currentQuesIndex);
            if (!wrapper.isHighlight) {
                wrapper.setHighlight();
                horizontalScrollView.invalidate();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class QuestionPagerAdapter extends PagerAdapter implements AnswerChoicesItem.OnAnswerChooseListener {

        private ArrayList<Question> data;
        private Context context;
        private OnQuestionPagerClickListener listener;

        public void setOnQuestionPagerClickListener(OnQuestionPagerClickListener listener) {
            this.listener = listener;
        }

        public QuestionPagerAdapter(Context context, ArrayList<Question> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = View.inflate(this.context, R.layout.written_test_page_item, null);
            Question ques = data.get(position);
            ImageView questionImage = (ImageView) view.findViewById(R.id.questionImage);
            TextView tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
            ImageView imgZoom = (ImageView) view.findViewById(R.id.buttonZoomIn);
            LinearLayout layoutAnswerChoiceContent = (LinearLayout) view.findViewById(R.id.layoutAnswerChoiceContent);
            tvQuestion.setText(ques.question);
            if (ques.image != null) {
                questionImage.setVisibility(View.VISIBLE);
                imgZoom.setVisibility(View.VISIBLE);
                questionImage.setImageBitmap(ques.image);
                questionImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(DMVWrittenTestFragment.KEY_DIALOG, data.get(position).image);
                        QuestionDialogFragment dialogFragment = new QuestionDialogFragment();
                        dialogFragment.setArguments(bundle);
                        dialogFragment.show(getBaseActivity().getSupportFragmentManager(), "Question");
                    }

                });
            } else {
                questionImage.setVisibility(View.GONE);
                imgZoom.setVisibility(View.GONE);
            }
            imgZoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DMVWrittenTestFragment.KEY_DIALOG, data.get(position).image);
                    QuestionDialogFragment dialogFragment = new QuestionDialogFragment();
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(getBaseActivity().getSupportFragmentManager(), "Question");
                }
            });
            ArrayList<AnswerChoicesItem> arrayList = makeChoices(ques);
            for (int i = 0; i < arrayList.size(); i++) {
                layoutAnswerChoiceContent.addView(arrayList.get(i).getView());
                LinearLayout.MarginLayoutParams marginParams = (LinearLayout.MarginLayoutParams) arrayList.get(i).getView().getLayoutParams();
                int margin = getResources().getDimensionPixelSize(R.dimen.common_size_5);
                marginParams.setMargins(margin, 0, margin, margin);
                arrayList.get(i).getView().requestLayout();
                arrayList.get(i).setOnAnswerChooseListener(this);
            }
            layoutAnswerChoiceContent.invalidate();
            container.addView(view);
            return view;
        }

        public ArrayList<AnswerChoicesItem> makeChoices(Question question) {
            ArrayList<AnswerChoicesItem> arrayList = new ArrayList<>();
            if (question.choiceA != null) {
                AnswerChoicesItem answerChoicesItemA = new AnswerChoicesItem(context, DataSource.ANSWER_A);
                answerChoicesItemA.setAnswer(question.choiceA);
                arrayList.add(answerChoicesItemA);
            }
            if (question.choiceB != null) {
                AnswerChoicesItem answerChoicesItemB = new AnswerChoicesItem(context, DataSource.ANSWER_B);
                answerChoicesItemB.setAnswer(question.choiceB);
                arrayList.add(answerChoicesItemB);
            }
            if (question.choiceC != null) {
                AnswerChoicesItem answerChoicesItemC = new AnswerChoicesItem(context, DataSource.ANSWER_C);
                answerChoicesItemC.setAnswer(question.choiceC);
                arrayList.add(answerChoicesItemC);
            }
            if (question.choiceD != null) {
                AnswerChoicesItem answerChoicesItemD = new AnswerChoicesItem(context, DataSource.ANSWER_D);
                answerChoicesItemD.setAnswer(question.choiceD);
                arrayList.add(answerChoicesItemD);
            }
            resetAllChoices(arrayList);
            if (question.myAnswer != DataSource.ANSWER_NOT_CHOSEN) {
                arrayList.get(question.myAnswer).setActive(true);
            }
            return arrayList;
        }

        public void resetAllChoices(ArrayList<AnswerChoicesItem> list) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setActive(false);
            }
        }

        @Override
        public void onAnswerChoose(AnswerChoicesItem item) {
            if (listener != null) {
                listener.OnPagerItemClick(item);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
