package app.library;

import app.service.biz.BizTeamService;

public class BackgroundBackThread extends Thread {
    public void setBizTeamService(BizTeamService bizTeamService) {
        this.bizTeamService = bizTeamService;
    }

    protected BizTeamService bizTeamService;


    @Override
    public void run() {
        bizTeamService.backup();
    }
}
