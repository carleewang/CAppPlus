package com.cpigeon.app.modular.matchlive.view.adapter;

import android.app.Activity;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cpigeon.app.R;
import com.cpigeon.app.modular.matchlive.model.bean.MatchInfo;
import com.cpigeon.app.modular.matchlive.model.bean.MatchPigeonsGP;
import com.cpigeon.app.modular.matchlive.model.bean.MatchPigeonsXH;
import com.cpigeon.app.modular.matchlive.view.activity.HistoryGradeActivity;
import com.cpigeon.app.modular.matchlive.view.activity.RaceReportActivity;
import com.cpigeon.app.modular.matchlive.view.fragment.HistoryGradeFragment;
import com.cpigeon.app.modular.matchlive.view.fragment.JiGeDataFragment;
import com.cpigeon.app.modular.matchlive.view.fragment.PigeonHouseLocationFragment;
import com.cpigeon.app.modular.matchlive.view.fragment.TrainingDataActivity;
import com.cpigeon.app.modular.matchlive.view.fragment.TrainingDataNewActivity;
import com.cpigeon.app.utils.EncryptionTool;
import com.cpigeon.app.utils.StringValid;

import java.util.ArrayList;
import java.util.List;

import static com.cpigeon.app.modular.matchlive.view.adapter.MatchLiveExpandAdapter.TYPE_DETIAL;
import static com.cpigeon.app.modular.matchlive.view.adapter.MatchLiveExpandAdapter.TYPE_TITLE;

/**
 * Created by Administrator on 2017/4/19.
 */

public class JiGeDataAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private String matchType;
    private int mc;
    private String name;
    private String footNumber;
    private MatchInfo matchInfo;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     */
    public JiGeDataAdapter(String matchType) {
        super(null);
        this.matchType = matchType;
        addItemType(TYPE_TITLE, R.layout.listitem_report_info);
        if ("xh".equals(matchType)) {
            addItemType(TYPE_DETIAL, R.layout.listitem_jige_info_expand);
        } else if ("gp".equals(matchType)) {
            addItemType(TYPE_DETIAL, R.layout.listitem_shanglong_info_expand);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        int mc = 0;
        switch (helper.getItemViewType()) {
            case TYPE_TITLE:
                helper.getView(R.id.report_info_item_mc_img).setVisibility(View.GONE);
                helper.setVisible(R.id.report_info_item_mc, true);

                if ("xh".equals(matchType)) {
                    JiGeTitleItem_XH titleItem = (JiGeTitleItem_XH) item;
                    mc = titleItem.getMatchPigeonsXH().getOrder();
                    name = titleItem.getMatchPigeonsXH().getName();
                    footNumber = EncryptionTool.decryptAES(titleItem.getMatchPigeonsXH().getFoot());
                    if (!StringValid.isStringValid(footNumber)) {
                        footNumber = titleItem.getMatchPigeonsXH().getFoot();
                    }

                } else {
                    JiGeTitleItem_GP titleItem = (JiGeTitleItem_GP) item;
                    mc = titleItem.getMatchPigeonsGP().getOrder();
                    name = titleItem.getMatchPigeonsGP().getName();
                    footNumber = EncryptionTool.decryptAES(titleItem.getMatchPigeonsGP().getFoot());
                    if (!StringValid.isStringValid(footNumber)) {
                        footNumber = titleItem.getMatchPigeonsGP().getFoot();
                    }
                }
                helper.setText(R.id.report_info_item_mc, mc + "");
                helper.setText(R.id.report_info_item_xm, name);
                helper.setText(R.id.report_info_item_hh, footNumber);
                break;
            case TYPE_DETIAL:

                if ("xh".equals(matchType)) {

                    final JiGeDetialItem_XH detialItem = (JiGeDetialItem_XH) item;
                    helper.setText(R.id.tv_jige_huiyuanpenghao, "会员棚号:" + detialItem.getSubItem(0).getPn());
                    helper.setText(R.id.tv_jige_saigeshijian, "集鸽时间:" + detialItem.getSubItem(0).getJgtime());
                    helper.setText(R.id.tv_jige_dengjizuobiao, "登记坐标:" + detialItem.getSubItem(0).getZx() + "/" + detialItem.getSubItem(0).getZy());
                    helper.setText(R.id.tv_jige_chazubaodao, "插组指定:" + detialItem.getSubItem(0).CZtoString());
                    helper.getView(R.id.history_grade).setOnClickListener(v -> {
                        try {
                            HistoryGradeActivity.start((Activity) mContext
                                    , EncryptionTool.decryptAES(detialItem.getSubItem(0).getFoot())
                                    , detialItem.getSubItem(0).getPn(), "xh"
                                    , ((RaceReportActivity)mContext).getMatchInfo()
                                    , 0,0d, detialItem.getSubItem(0).getName(),0);
                        } catch (Exception e) {
                            HistoryGradeActivity.start((Activity) mContext
                                    , detialItem.getSubItem(0).getFoot()
                                    , detialItem.getSubItem(0).getPn(), "xh"
                                    , matchInfo
                                    , 0,0d, detialItem.getSubItem(0).getName(),0);
                        }
                    });

                    helper.setVisible(R.id.pigeonHouse, true);
                    helper.getView(R.id.pigeonHouse).setOnClickListener(v -> {
                        PigeonHouseLocationFragment.start((Activity)mContext, detialItem.getSubItem(0).getZx()
                                ,detialItem.getSubItem(0).getZy(), detialItem.getSubItem(0).getName());
                    });
                } else {
                    final JiGeDetialItem_GP detialItem = (JiGeDetialItem_GP) item;
                    helper.setText(R.id.tv_shanglong_saigeyuse, "赛鸽羽色:" + detialItem.getSubItem(0).getColor());
                    helper.setText(R.id.tv_shanglong_suoshuarea, "所属地区:" + detialItem.getSubItem(0).getArea());
                    helper.setText(R.id.tv_shanglong_shanglongshijian, "上笼时间:" + detialItem.getSubItem(0).getJgtime());
                    helper.setText(R.id.tv_shanglong_shangchuanshijian, "上传时间:" + detialItem.getSubItem(0).getUptime());
                    helper.setText(R.id.tv_shanglong_dianzihuanhao, "电子环号:" + detialItem.getSubItem(0).getRing());
                    helper.setText(R.id.tv_shanglong_suoshutuandui, "所属团队:" + detialItem.getSubItem(0).getTtzb());
                    helper.setText(R.id.tv_shanglong_chazuzhiding, "插组指定:" + detialItem.getSubItem(0).CZtoString());
                    helper.itemView.setOnClickListener(v -> {
                        try {
                            TrainingDataNewActivity.start((Activity) mContext
                                    , ((RaceReportActivity)mContext).getMatchInfo()
                                    ,detialItem.getSubItem(0).getFoot()
                            ,null, null, null, detialItem.getSubItem(0).getName());
                        } catch (Exception e) {
                            TrainingDataNewActivity.start((Activity) mContext
                                    , matchInfo
                                    ,EncryptionTool.encryptAES(detialItem.getSubItem(0).getFoot())
                                    ,null, null, null, detialItem.getSubItem(0).getName());
                        }
                    });
                }

                break;

        }
    }

    public static List<MultiItemEntity> getXH(List<MatchPigeonsXH> data) {
        List<MultiItemEntity> result = new ArrayList<>();
        if (data == null)
            return result;
        JiGeTitleItem_XH titleItem;
        JiGeDetialItem_XH detialItem;
        if (data.size() > 0) {
            for (MatchPigeonsXH matchInfo : data) {
                titleItem = new JiGeTitleItem_XH(matchInfo);

                detialItem = new JiGeDetialItem_XH();
                detialItem.addSubItem(matchInfo);

                titleItem.addSubItem(detialItem);
                result.add(titleItem);
            }
        }

        return result;
    }

    public static List<MultiItemEntity> getGP(List<MatchPigeonsGP> data) {
        List<MultiItemEntity> result = new ArrayList<>();
        if (data == null)
            return result;
        JiGeTitleItem_GP titleItem;
        JiGeDetialItem_GP detialItem;
        if (data.size() > 0) {
            for (MatchPigeonsGP matchInfo : data) {
                titleItem = new JiGeTitleItem_GP(matchInfo);

                detialItem = new JiGeDetialItem_GP();
                detialItem.addSubItem(matchInfo);

                titleItem.addSubItem(detialItem);
                result.add(titleItem);
            }
        }

        return result;
    }

    public static class JiGeTitleItem_XH extends AbstractExpandableItem<JiGeDetialItem_XH> implements MultiItemEntity {

        MatchPigeonsXH matchPigeonsXH;

        public JiGeTitleItem_XH(MatchPigeonsXH matchPigeonsXH) {
            this.matchPigeonsXH = matchPigeonsXH;
        }

        public MatchPigeonsXH getMatchPigeonsXH() {
            return matchPigeonsXH;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getItemType() {
            return TYPE_TITLE;
        }
    }

    public static class JiGeDetialItem_XH extends AbstractExpandableItem<MatchPigeonsXH> implements MultiItemEntity {

        @Override
        public int getItemType() {
            return TYPE_DETIAL;
        }

        @Override
        public int getLevel() {
            return 1;
        }
    }


    public static class JiGeTitleItem_GP extends AbstractExpandableItem<JiGeDetialItem_GP> implements MultiItemEntity {

        MatchPigeonsGP matchPigeonsGP;

        public JiGeTitleItem_GP(MatchPigeonsGP matchPigeonsGP) {
            this.matchPigeonsGP = matchPigeonsGP;
        }

        public MatchPigeonsGP getMatchPigeonsGP() {
            return matchPigeonsGP;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getItemType() {
            return TYPE_TITLE;
        }
    }

    public static class JiGeDetialItem_GP extends AbstractExpandableItem<MatchPigeonsGP> implements MultiItemEntity {

        @Override
        public int getItemType() {
            return TYPE_DETIAL;
        }

        @Override
        public int getLevel() {
            return 1;
        }
    }

    public void setMatchInfo(MatchInfo matchInfo) {
        this.matchInfo = matchInfo;
    }
}
