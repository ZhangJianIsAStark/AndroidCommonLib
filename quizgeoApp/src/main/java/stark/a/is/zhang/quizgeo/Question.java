package stark.a.is.zhang.quizgeo;

/**
 * A class to describe questions
 * TextResId refer to the res id of question-stem
 * AnswerIsTrue refer to the answer
 * */
class Question {
    private int mTextResId;
    private boolean mAnswerTrue;

    Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
    }

    int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}
