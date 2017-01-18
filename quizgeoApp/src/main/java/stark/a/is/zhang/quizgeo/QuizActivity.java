package stark.a.is.zhang.quizgeo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import stark.a.is.zhang.activity.BaseActivity;

public class QuizActivity extends BaseActivity {
    private Button mTrueButton;
    private Button mFalseButton;

    private TextView mQuestionTextView;
    private Button mNextButton;

    private Question[] mQuestionBank;

    private int mCurrentIndex = 0;

    @Override
    protected void initVariables() {
        mQuestionBank = new Question[] {
                new Question(R.string.question_oceans, true),
                new Question(R.string.question_mideast, false),
                new Question(R.string.question_africa, false),
                new Question(R.string.question_americas, true),
                new Question(R.string.question_asia, true)
        };
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_quiz);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
    }

    @Override
    protected void prepareData() {
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean ansIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int toastResId;
        if (ansIsTrue == userPressedTrue) {
            toastResId = R.string.correct_toast;
        } else {
            toastResId = R.string.incorrect_toast;
        }

        Toast.makeText(QuizActivity.this, toastResId, Toast.LENGTH_SHORT).show();
    }
}
