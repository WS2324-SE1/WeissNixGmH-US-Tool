import java.io.Serializable;
import java.util.UUID;

public class UserStory implements Comparable<UserStory>, Serializable
{

    String mTitel;

    String mDescription;

    String mAcceptanceCriteria;
    int mExpense = 0;
    UUID mId;
    int mAddedValue = 0;
    int mRisk = 0;
    int mPenalty = 0;
    double mPriority = 0.0;
    String mProject;

    public UserStory(UUID id, String titel, int added_value, int penalty,
                     int expense, int risk, double prio)
    {
        this.mId = id;
        this.mTitel = titel;
        this.mAddedValue = added_value;
        this.mPenalty = penalty;
        this.mExpense = expense;
        this.mRisk = risk;
        this.mPriority = prio;
    }

    public UserStory()
    {
    }

    public String getProject()
    {
        return mProject;
    }

    public void setProject(String project)
    {
        this.mProject = project;
    }

    public double getPriority()
    {
        return mPriority;
    }

    public void setPriority(double mPriority)
    {
        this.mPriority = mPriority;
    }

    public void calculatePriority()
    {
        this.mPriority = (double) (this.mAddedValue - this.mPenalty) / (double) (this.mExpense + this.mRisk);
    }

    public String getTitel()
    {
        return mTitel;
    }

    public void setTitel(String mTitel)
    {
        this.mTitel = mTitel;
    }

    public int getExpense()
    {
        return mExpense;
    }

    public void setExpense(int mExpense)
    {
        this.mExpense = mExpense;
    }

    public UUID getId()
    {
        return mId;
    }

    public void setId(UUID mId)
    {
        this.mId = mId;
    }

    public int getAddedValue()
    {
        return mAddedValue;
    }

    public void setAddedValue(int mAddedValue)
    {
        this.mAddedValue = mAddedValue;
    }

    public int getRisk()
    {
        return mRisk;
    }

    public void setRisk(int mRisk)
    {
        this.mRisk = mRisk;
    }

    public int getPenalty()
    {
        return mPenalty;
    }

    public void setPenalty(int mPenalty)
    {
        this.mPenalty = mPenalty;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String description)
    {
        this.mDescription = description;
    }

    public String getAcceptanceCriteria()
    {
        return mAcceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria)
    {
        this.mAcceptanceCriteria = acceptanceCriteria;
    }


    public String toString(){
        return this.mTitel + "@" + this.mProject + " - " + this.mId.toString().split("-")[0];
    }

    /*
     * Methode zum Vergleich zweier UserStories.
     * Vergleich ist implementiert auf Basis des Vergleichs
     * von zwei Prio-Werten.
     */
    public int compareTo(UserStory input)
    {
        if (input.getPriority() == this.getPriority())
        {
            return 0;
        }

        if (input.getPriority() > this.getPriority())
        {
            return 1;
        } else return -1;
    }

}

