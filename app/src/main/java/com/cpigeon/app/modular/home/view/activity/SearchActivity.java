package com.cpigeon.app.modular.home.view.activity;

/**
 * Created by chenshuai on 2017/4/26.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpigeon.app.R;
import com.cpigeon.app.commonstandard.view.activity.BaseActivity;
import com.cpigeon.app.modular.home.model.bean.SearchHistory;
import com.cpigeon.app.modular.home.presenter.SearchPresenter;
import com.cpigeon.app.modular.home.view.activity.viewdao.ISearchView;
import com.cpigeon.app.modular.matchlive.model.bean.MatchInfo;
import com.cpigeon.app.modular.matchlive.view.activity.RaceReportActivity;
import com.cpigeon.app.modular.matchlive.view.adapter.HistroyAdapter;
import com.cpigeon.app.modular.matchlive.view.adapter.SearchResultAdapter;
import com.cpigeon.app.utils.BaseRecyclerViewAdapter;
import com.cpigeon.app.utils.BaseRecyclerViewViewHolder;
import com.cpigeon.app.utils.CPigeonApiUrl;
import com.cpigeon.app.utils.CpigeonConfig;
import com.cpigeon.app.utils.NetUtils;
import com.cpigeon.app.utils.ScreenTool;
import com.cpigeon.app.utils.SharedPreferencesTool;
import com.cpigeon.app.utils.ToastUtil;
import com.cpigeon.app.utils.customview.CpigeonListView;
import com.cpigeon.app.utils.customview.SearchEditText;
import com.cpigeon.app.utils.customview.SearchTitleBar;

import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/4/14.
 */

public class SearchActivity extends BaseActivity<SearchPresenter> implements ISearchView {
    private String sKey = "";
    public static final String INTENT_KEY_SEARCHKEY = "search_key";//传递过来的搜索关键字
    public static final String INTENT_KEY_SEARCH_HINT_TEXT = "search_hint_text";//输入框提示
    @BindView(R.id.search_bar)
    SearchTitleBar searchBar;
    @BindView(R.id.list_history)
    CpigeonListView listHistory;
    @BindView(R.id.tv_history_clear)
    TextView tvHistoryClear;
    @BindView(R.id.layout_search_history)
    ScrollView layoutSearchHistory;
    @BindView(R.id.tv_search_result)
    TextView tvSearchResult;
    @BindView(R.id.recyclerview_searchReault)
    RecyclerView recyclerviewSearchReault;
    @BindView(R.id.layout_search_result)
    LinearLayout layoutSearchResult;
    private HistroyAdapter mHistroyAdapter;
    private SearchResultAdapter mSearchResultAdapter;
    private SearchEditText search_edittext;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public SearchPresenter initPresenter() {
        return new SearchPresenter(this);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        search_edittext = searchBar.getTitleBarSearchEditText();
        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String lastKey = sKey;
                sKey = s.toString().trim();
                if (!lastKey.equals(sKey)) {
                    mPresenter.showHistory();
                }
            }
        });

        searchBar.setOnTitleBarClickListener(new SearchTitleBar.OnSearchTitleBarClickListener() {
            @Override
            public void onSearchClick(View view, String keyword) {
                setsKey(keyword);
            }

            @Override
            public void onLeftClick() {
                finish();
            }
        });
        mHistroyAdapter = new HistroyAdapter(mContext);
        listHistory.setMaxHeight((int) (ScreenTool.getScreenHeight(mContext) * 0.7));
        listHistory.setAdapter(mHistroyAdapter);
        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setsKey(mHistroyAdapter.getItem(position).getSearchKey());
            }
        });
        mSearchResultAdapter = new SearchResultAdapter(mContext);
        mSearchResultAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, int viewType) {
                if (viewType == SearchResultAdapter.VIEWTYPE_MATCHINFO) {
                    Map<String, Object> data = ((BaseRecyclerViewViewHolder<Map<String, Object>>) viewHolder).getCurrData();
                    Intent intent = new Intent(SearchActivity.this, RaceReportActivity.class);
                    Bundle bundle = new Bundle();                           //创建Bundle对象
                    bundle.putSerializable("matchinfo", (MatchInfo) data.get("data"));     //装入数据
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(mContext);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerviewSearchReault.setLayoutManager(lm);
        recyclerviewSearchReault.setAdapter(mSearchResultAdapter);
        layoutSearchResult.setVisibility(View.GONE);
        initData();
    }

    private void initData() {
        String s = getIntent().getStringExtra(INTENT_KEY_SEARCH_HINT_TEXT);
        if (!TextUtils.isEmpty(s))
            search_edittext.setHint(s);
        s = getIntent().getStringExtra(INTENT_KEY_SEARCHKEY);
        search_edittext.requestFocus();
        if (!TextUtils.isEmpty(s))
            setsKey(s);
        else
            mPresenter.showHistory();
    }


    /**
     * 显示搜索结果
     *
     * @param data
     */
    @Override
    public void showLoadSearchResult(List data) {
        layoutSearchHistory.setVisibility(View.GONE);
        layoutSearchResult.setVisibility(View.VISIBLE);
        if (data.size() == 0) {
            tvSearchResult.setVisibility(View.GONE);
            mSearchResultAdapter.setStatus(SearchResultAdapter.STATUS_NODATA);
        } else {
            tvSearchResult.setVisibility(View.VISIBLE);
            mSearchResultAdapter.setStatus(BaseRecyclerViewAdapter.STATUS_DEFAULT);
        }
        mSearchResultAdapter.setDataList(data);
    }

    /**
     * 加载搜索历史
     *
     * @param data
     */
    @Override
    public void showLoadSearchHistory(List<SearchHistory> data) {
        if (data == null || data.size() == 0) {
            layoutSearchHistory.setVisibility(View.GONE);
            tvHistoryClear.setVisibility(View.GONE);
            return;
        } else {
            layoutSearchHistory.setVisibility(View.VISIBLE);
            tvHistoryClear.setVisibility(View.VISIBLE);
            mHistroyAdapter.setDataList(data);
        }
        layoutSearchResult.setVisibility(View.GONE);
    }

    @Override
    public String getSearch() {
        return sKey;
    }

    @OnClick(R.id.tv_history_clear)
    public void onViewClicked() {

        mPresenter.deleteHistory();
        mPresenter.showHistory();

    }


    private void setsKey(@NonNull String key) {
        this.sKey = key;
        if (!judgmentAndExecutionCommand(key)) {
            mPresenter.doSearch();
        } else {
            this.sKey = "";
        }
        search_edittext.setText(sKey);
        search_edittext.setSelection(search_edittext.getText().toString().length());//将光标移至文字末尾
    }

    /**
     * 判断并执行隐藏命令
     *
     * @param str
     * @return 是否执行隐藏命令
     */
    private boolean judgmentAndExecutionCommand(String str) {
        boolean isStart = false;
        if (!TextUtils.isEmpty(str) && str.startsWith("://")) {
            isStart = true;
            switch (str.substring(3, str.length()).toLowerCase()) {
                case "currserver":
                    ToastUtil.showToast(this, CPigeonApiUrl.getInstance().getServer(), Toast.LENGTH_SHORT);
                    break;
                case "changeserver":
                    ToastUtil.showToast(this, CPigeonApiUrl.getInstance().getServer(true), Toast.LENGTH_SHORT);
                    break;
                case "initdb":
                    try {
                        x.getDb(CpigeonConfig.getDataDb()).dropDb();
                        x.getDb(CpigeonConfig.getDataDbforNews()).dropDb();
                        ToastUtil.showToast(this, "初始化成功", Toast.LENGTH_SHORT);
                    } catch (DbException e) {
                        e.printStackTrace();
                        ToastUtil.showToast(this, "初始化错误", Toast.LENGTH_SHORT);
                    }
                    break;
                case "reguide":
                    SharedPreferencesTool.Save(this, "guide_version", "");
                    break;
                default:
                    isStart = false;
            }
        }
        return isStart;
    }
}