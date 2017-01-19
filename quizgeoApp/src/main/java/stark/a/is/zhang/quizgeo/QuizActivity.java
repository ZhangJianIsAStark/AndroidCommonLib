package stark.a.is.zhang.quizgeo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import stark.a.is.zhang.activity.BaseActivity;

public class QuizActivity extends BaseActivity {
    private Button mTrueButton;
    private Button mFalseButton;

    private TextView mQuestionTextView;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private Button mCheatButton;

    private Question[] mQuestionBank;

    private int mCurrentIndex = 0;
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEAT = "cheat";

    private static final int REQUEST_CODE_CHEAT = 0;
    private boolean[] mIsCheater;

    @Override
    protected void initVariables() {
        mQuestionBank = new Question[] {
                new Question(R.string.question_oceans, true),
                new Question(R.string.question_mideast, false),
                new Question(R.string.question_africa, false),
                new Question(R.string.question_americas, true),
                new Question(R.string.question_asia, true)
        };

        mIsCheater = new boolean[mQuestionBank.length];
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_quiz);

        //When rotate screen, etc.
        //Recover the value about currentIndex and cheater
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBooleanArray(KEY_CHEAT);
        }

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
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAnotherQuestion(true);
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAnotherQuestion(false);
            }
        });


        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAnotherQuestion(true);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = CheatActivity.newIntent(QuizActivity.this,
                        mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(i, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    @Override
    protected void prepareData() {
    }

    private void moveToAnotherQuestion(boolean moveToNext) {
        int step = moveToNext ? 1 : -1;
        mCurrentIndex = (mCurrentIndex + mQuestionBank.length + step) % mQuestionBank.length;

        updateQuestion();
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean ansIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int toastResId;

        if (mIsCheater[mCurrentIndex]) {
            toastResId = R.string.judgement_toast;
        } else if (ansIsTrue == userPressedTrue) {
            toastResId = R.string.correct_toast;
        } else {
            toastResId = R.string.incorrect_toast;
        }

        Toast.makeText(QuizActivity.this, toastResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_CHEAT, mIsCheater);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data != null) {
                mIsCheater[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            }
        }
    }
}
