import java.io.Serializable;

    public class UserStory implements Comparable<UserStory>, Serializable {

        String mTitel;
        int mExpense = 0;
        int mId = 0;
        int mAddedValue = 0;
        int mRisk = 0;
        int mPenalty = 0;
        double mPriority = 0.0;

        public String getProject() {
            return mProject;
        }

        public void setProject(String project) {
            this.mProject = project;
        }

        String mProject;


        public UserStory(int id, String titel, int added_value, int penalty,
                         int expense, int risk, double prio) {
            this.mId = id;
            this.mTitel = titel;
            this.mAddedValue = added_value;
            this.mPenalty = penalty;
            this.mExpense = expense;
            this.mRisk = risk;
            this.mPriority = prio;
        }

        public UserStory() {
        }

        public double getPriority() {
            return mPriority;
        }

        public void setPriority(double mPriority) {
            this.mPriority = mPriority;
        }

        public String getTitel() {
            return mTitel;
        }
        public void setTitel(String mTitel) {
            this.mTitel = mTitel;
        }
        public int getExpense() {
            return mExpense;
        }
        public void setExpense(int mExpense) {
            this.mExpense = mExpense;
        }
        public int getId() {
            return mId;
        }
        public void setId(int mId) {
            this.mId = mId;
        }
        public int getAddedValue() {
            return mAddedValue;
        }
        public void setAddedValue(int mAddedValue) {
            this.mAddedValue = mAddedValue;
        }
        public int getRisk() {
            return mRisk;
        }
        public void setRisk(int mRisk) {
            this.mRisk = mRisk;
        }
        public int getPenalty() {
            return mPenalty;
        }
        public void setPenalty(int mPenalty) {
            this.mPenalty = mPenalty;
        }

        /*
         * Methode zum Vergleich zweier UserStories.
         * Vergleich ist implementiert auf Basis des Vergleichs
         * von zwei Prio-Werten.
         */
        public int compareTo(UserStory input) {
            if ( input.getPriority() == this.getPriority() ) {
                return 0;
            }

            if ( input.getPriority() > this.getPriority() ) {
                return 1;
            }
            else return -1;
        }

    }

