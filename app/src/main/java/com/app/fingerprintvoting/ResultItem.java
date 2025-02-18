package com.app.fingerprintvoting;

public class ResultItem {
    private final String candidateName;
    private final int voteCount;

    public ResultItem(String candidateName, int voteCount) {
        this.candidateName = candidateName;
        this.voteCount = voteCount;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public int getVoteCount() {
        return voteCount;
    }
}
