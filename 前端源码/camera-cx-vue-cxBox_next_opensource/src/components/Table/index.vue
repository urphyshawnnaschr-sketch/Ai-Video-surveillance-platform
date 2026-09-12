<template>
  <div v-loading="loading" class="common-table">
    <div v-if="showTableHeaderSlot" class="common-table-header">
      <slot name="header"></slot>
    </div>
    <div class="table-container">
      <slot name="header-btn"></slot>
      <div>
        <el-table
          ref="gm-table"
          :key="tableKey"
          :data="tableDataSource"
          :show-header="showHeader"
          :fit="fit"
          :lazy="lazy"
          :load="load"
          :row-key="rowKey"
          :row-class-name="rowClassName"
          :default-expand-all="defaultExpandAll"
          :tree-props="treeProps"
          :empty-text="loading ? $t('marktool.annotate.6emt1s') : emptyText"
          :size="size"
          :highlight-current-row="highlightCurrentRow"
          :border="border"
          :height="height"
          :max-height="maxHeight"
          :indent="indent"
          :header-cell-style="headerCellStyle"
          :span-method="mergeTableCell"
          :show-summary="showSummary"
          :summary-method="summaryMethod"
        >
          <generate-table-column
            v-for="(column, index) of tableColumns"
            :key="index"
            :column="column"
            :$columnIndex="index"
          >
            <template #tableColumnHeader="$scope">
              <template v-if="$scope.$column.type !== 'checkbox'">
                <template v-if="$scope.$column.headerSlot">
                  <slot :name="$scope.$column.headerSlot"></slot>
                </template>
                <template v-else-if="$scope.$column.renderHeader">
                  <render-table-header
                    :render-handler="$scope.$column.renderHeader"
                    :scope-data="$scope"
                  ></render-table-header>
                </template>
                <span v-else>{{ $scope.$column.title }}</span>
              </template>
              <template v-else>
                <el-checkbox
                  :value="checkAll.checked"
                  :indeterminate="checkAll.indeterminate"
                  @change="checkAllChange"
                ></el-checkbox>
              </template>
            </template>
            <template #default="$scope">
              <div v-if="$scope.$column.type">
                <template v-if="$scope.$column.type === 'radio'">
                  <el-radio
                    :value="
                      selections.find(
                        (item) =>
                          item === getRowKeyValue($scope.row, $scope.$index)
                      )
                    "
                    :label="getRowKeyValue($scope.row, $scope.$index)"
                    :disabled="renderSelectionDisabled($scope, $scope.$column)"
                    @change="radioChange($scope)"
                  >
                    <!-- 占位元素 -->
                    <span
                      style="
                        opacity: 0;
                        display: inline-block;
                        width: 0;
                        height: 0;
                      "
                    ></span>
                  </el-radio>
                </template>
                <template v-else-if="$scope.$column.type === 'checkbox'">
                  <el-checkbox
                    :value="
                      selections.includes(
                        getRowKeyValue($scope.row, $scope.$index)
                      )
                    "
                    :disabled="renderSelectionDisabled($scope, $scope.$column)"
                    @change="checkboxChange($event, $scope)"
                  ></el-checkbox>
                </template>
              </div>
              <div v-else>
                <template v-if="$scope.$column.render">
                  <render-table-cell-view
                    :render-handler="$scope.$column.render"
                    :scope-data="$scope"
                    :props-key="$scope.$column.key"
                  ></render-table-cell-view>
                </template>
                <template v-else-if="$scope.$column.slot">
                  <slot
                    :name="$scope.$column.slot"
                    :row="$scope.row"
                    :$index="$scope.$index"
                    :column="$scope.column"
                    :value="$scope.row[$scope.$column['key']]"
                    :$column="$scope.$column"
                  ></slot>
                </template>
                <template v-else>
                  <el-tooltip
                    placement="top-start"
                    :content="String($scope.row[column.key])"
                    :open-delay="300"
                    :manual="!$scope.$column.ellipsis"
                  >
                    <div :class="{ ellipsis: $scope.$column.ellipsis }">
                      {{ $scope.row[column.key] }}
                    </div>
                  </el-tooltip>
                </template>
              </div>
            </template>
          </generate-table-column>
          <div v-if="showEmptyTextSlot" slot="empty">
            <slot name="emptyText"></slot>
          </div>
        </el-table>
        <div
          v-if="showPagination"
          style="
            display: flex;
            justify-content: flex-end;
            align-items: center;
            padding: 10px 0;
          "
        >
          <!--          <div>-->
          <!--            <span v-if="rowSelection && selections.length" style="letter-spacing: 1px;font-size: 12px">已勾选{{ selections.length }}条</span>-->
          <!--          </div>-->
          <el-pagination
            :layout="paginationConfig.layout"
            :small="paginationConfig.small"
            :page-size.sync="paginationConfig.pageSize"
            :total="paginationConfig.total"
            :pager-count="paginationConfig.pagerCount"
            :current-page.sync="paginationConfig.currentPage"
            :page-sizes="paginationConfig.pageSizes"
            :prev-text="paginationConfig.prevText"
            :next-text="paginationConfig.nextText"
            :disabled="paginationConfig.disabled"
            :hide-on-single-page="paginationConfig.hideOnSinglePage"
            @size-change="pageSizeChange"
            @current-change="currentPageChange"
          ></el-pagination>
        </div>
      </div>

      <!-- loading 层 -->
      <!--      <div v-if="loading" class="table-loading">-->
      <!--        <i class="el-icon-loading"></i>-->
      <!--      </div>-->
    </div>
  </div>
</template>

<script>
/**
 * @description: 表格
 * ** slot 插槽可选值 ************************************************************************
 * @slot header              作用于表格上面
 * @slot emptyText           空数据是展示的文本
 * ** 说明 *****************************************************************************
 * @props columns                   { Array<ColumnsType> }  表头配置
 * @props dataSource                { Array<Object> }       数据源
 * @props loading                   { Boolean }             是否正在加载
 * @props pagination                { PaginationType }      分页配置
 * @props showHeader                { Boolean }             是否显示表头
 * @props border                    { Boolean }             是否带有纵向边框
 * @props height                    { String/Number }       Table 的高度，默认为自动高度
 * @props maxHeight                 { String/Number }       Table 的最大高度
 * @props fit                       { Boolean }             列的宽度是否自撑开
 * @props highlightCurrentRow       { Boolean }             列的宽度是否自撑开
 * @props rowKey                    { Function(row)/String } 行数据的 Key，用来优化 Table 的渲染
 * @props size                      { String }              Table 的尺寸   medium / small / mini
 * @props emptyText                 { String }              空数据时显示的文本内容，也可以通过 slot="empty" 设置
 * @props indent                    { Number }              展示树形数据时，树节点的缩进
 * @props rowSelection              { RowSelectionType/Boolean }  列表项是否可选择
 * @props spanMethod                { Function() }          合并单元格属性
 * @props selections                { Array<string|number> }  // 选中的值 只有在配置 rowSelection 时才会生效
 *
 * ** emit 事件 ******************************************************************************
 * @emits pageChange  (currentPage, pageSize) => void
 * ******************************************************************************************
 * interface ColumnsType {
 *   align?: string                     // 设置列内容的对齐方式
 *   ellipsis?: Boolean                 // 超过宽度将自动省略
 *   key?: string                       // 列数据在数据项中对应的 key
 *   title: string                      // 列头显示文字
 *   width?: string | number            // 列宽度
 *   fixed?: Boolean | string           // 列是否固定  可选 true(等效于 left) 'left' 'right'
 *   render?: Function(函数, params) {} // 生成复杂数据的渲染函数  params = { $index: 行索引, value: 当前单元格数据, row: 当前行数据 }
 *   slot?: string                      // 可以通过该属性配置支持 slot-scope 的属性  详情见 element 的值 但是多
 *                                         【接上面一行】 了一个当前字段，代表当前单元格的字段
 *   merger?: boolean | string          // 是否通过值上下合并, 如果传入 span-method 则不生效 如果传入 string 类型 则通过传入的值当做 key 去合并
 *   children?: Array<ColumnsType>      // 生成多级表头
 * }
 * interface PaginationType {
 *   small?: Boolean                    // 是否使用小型分页样式
 *   background?: Boolean               // 是否为分页按钮添加背景色
 *   pageSize?: number                  // 每页显示条目个数
 *   total?: number                     // 总条目数
 *   pageCount?: number                 // 总页数
 *   pagerCount?: number                // 页码按钮的数量，当总页数超过该值时会折叠  大于等于 5 且小于等于 21 的奇数
 *   currentPage?: number               // 当前页数
 *   layout?: String                    // 组件布局，子组件名用逗号分隔  sizes, prev, pager, next, jumper, ->, total, slot
 *   pageSizes?: number[]               // 每页显示个数选择器的选项设置  [10, 20, 30, 40, 50, 100]
 *   prevText?: string                  // 替代图标显示的上一页文字
 *   nextText?: string                  // 替代图标显示的下一页文字
 *   disabled?: boolean                 // 是否禁用
 *   hideOnSinglePage?: boolean         // 只有一页时是否隐藏
 * }
 * interface RowSelectionType {
 *   type?: string                       // 多选/单选，checkbox or radio
 *   width?: string|number               // 自定义列表选择框宽度
 *   title?: string|VNode|slot           // 自定义列表选择框标题
 *   fixed?: Boolean                     // 把选择框列固定在左边
 *   change?: (选择的值, 选中的行) => {}     //change 回调函数
 *   disabled?: (当前行数据, index) => Boolean     // 是否禁用
 * }
 */
import GenerateTableColumn from "./GenerateTableColumn";

const hasValue = (v) => v === 0 || v;

// 默认的分页配置
const defaultPaginationConfig = {
  total: 0,
  pageSize: 10,
  currentPage: 1,
  pagerCount: 5,
  pageSizes: [5, 10, 20, 30, 40, 50],
  layout: "total, sizes, prev, pager, next, jumper",
};

// 渲染表头
const RenderTableHeader = {
  props: {
    renderHandler: {
      type: Function,
    },
    scopeData: {
      type: Object,
    },
  },
  render(createElement, context) {
    return this.renderHandler(createElement, this.scopeData);
  },
};

// template 里面没有办法直接使用需要中间层去处理
const RenderTableCellView = {
  props: {
    renderHandler: {
      type: Function,
    },
    scopeData: {
      type: Object,
    },
    propsKey: {
      type: String,
    },
  },
  render(createElement) {
    if (typeof this.renderHandler !== "function") return null;
    return this.renderHandler(createElement, {
      ...this.scopeData,
      value: this.scopeData.row[this.propsKey],
    });
  },
};

const toString = (v) => "" + v;

/**
 * @description 表格处理方法
 */
const isExist = (item, form) => {
  for (const [key, value] of Object.entries(form)) {
    if (value !== 0 && !value) continue;
    if (typeof value === "function") {
      const r = value(item, key);
      if (!r) return false;
    } else {
      const rowValue = item[key];
      if (rowValue === value) continue;
      if (!rowValue && value) return false;
      if (toString(rowValue).indexOf(toString(value)) === -1) return false;
    }
  }
  return true;
};

export default {
  props: {
    columns: {
      type: Array,
      default: () => [],
    },
    dataSource: {
      type: Array,
      default: () => [],
    },
    lazy: {
      type: Boolean,
      default: false,
    },
    tableKey: {
      type: String,
      default: null,
    },
    load: {
      type: Function,
      default: null,
    },
    defaultExpandAll: {
      type: Boolean,
      default: false,
    },
    treeProps: {
      type: Object,
      default: () => ({ children: "children", hasChildren: "hasChildren" }),
    },
    loading: {
      type: Boolean,
      default: false,
    },
    pagination: {
      type: [Object, Boolean],
      default: () => defaultPaginationConfig,
    },
    showHeader: {
      type: Boolean,
      default: true,
    },
    border: {
      type: Boolean,
      default: true,
    },
    fit: {
      type: Boolean,
      default: true,
    },
    highlightCurrentRow: {
      type: Boolean,
      default: false,
    },
    size: {
      type: String,
      default: "mini",
    },
    emptyText: {
      type: String,
    },
    indent: {
      type: Number,
      default: 16,
    },
    rowKey: {
      type: [Function, String],
    },
    rowClassName: {
      type: [Function, String],
    },
    maxHeight: {
      type: [Number, String],
    },
    height: {
      type: [Number, String],
    },
    rowSelection: {
      type: [Boolean, Object],
      default: false,
    },
    spanMethod: {
      type: Function,
    },
    headerCellStyle: {
      type: Function,
    },
    selections: {
      type: Array,
      default: () => [],
    },
    filterForm: {
      type: Object,
      default: () => {},
    },
    showSummary: {
      type: Boolean,
      default: false,
    },
    summaryMethod: {
      type: Function,
    },
  },
  components: {
    RenderTableCellView,
    GenerateTableColumn,
    RenderTableHeader,
  },
  computed: {
    tableDataSource() {
      const dataSource = this.dataSource;
      const filterForm = this.filterForm;
      if (!filterForm || !Object.keys(filterForm).length) return dataSource;
      return dataSource.filter((item) => isExist(item, filterForm));
    },
    tableColumns() {
      return this.mergeTableColumns();
    },
    walkTableColumns() {
      return this.tableColumns.reduce((prev, item) => {
        const { children } = item;
        if (children && children.length > 0) {
          prev.push(...children);
        } else {
          prev.push(item);
        }
        return prev;
      }, []);
    },
    showPagination() {
      return typeof this.pagination !== "boolean" && this.pagination !== false;
    },
    showTableHeaderSlot() {
      return !!this.$slots.header;
    },
    showEmptyTextSlot() {
      return !!this.$slots.emptyText;
    },
    paginationConfig() {
      return Object.assign({}, defaultPaginationConfig, this.pagination || {});
    },
    checkAll() {
      let index = -1;
      let length = this.dataSource.length;
      const disabled = this.rowSelection.disabled;
      const result = {
        indeterminate: false,
        checked: true,
      };
      if (this.dataSource.length === 0 || disabled === true)
        return {
          indeterminate: false,
          checked: false,
        };
      while (++index < length) {
        const item = this.dataSource[index];
        if (typeof disabled === "function" && disabled(item, index)) continue;
        const $r = this.selections.includes(this.getRowKeyValue(item, index));
        if ($r) {
          result.indeterminate = true;
        }
        if (!$r) {
          result.checked = false;
        }
      }
      if (result.checked) {
        result.indeterminate = false;
      }
      return result;
    },
  },
  methods: {
    // 合并单元格属性
    mergeTableCell({ row, column, rowIndex, columnIndex }) {
      // 如果传入 spanMethod 则优先级最高
      if (this.spanMethod && typeof this.spanMethod === "function")
        return this.spanMethod(...arguments);
      const tableColumn = this.walkTableColumns[columnIndex];
      const { merger } = tableColumn;
      const defaultMerger = {
        rowspan: 1,
        colspan: 1,
      };
      if (!merger) return defaultMerger;
      const key = typeof merger === "string" ? merger : tableColumn.key;
      if (!key) return defaultMerger;
      let rowspan = 0;
      defaultMerger.rowspan = rowspan;
      const prevIndex = rowIndex - 1;

      if (
        prevIndex > -1 &&
        hasValue(this.dataSource[prevIndex][key]) &&
        hasValue(row[key]) &&
        this.dataSource[prevIndex][key] === row[key]
      ) {
        return defaultMerger;
      }
      rowspan = 1;
      let index = rowIndex;
      while (++index < this.dataSource.length) {
        const item = this.dataSource[index];
        if (!hasValue(item[key])) break;
        if (item[key] !== row[key]) break;
        rowspan++;
      }
      defaultMerger.rowspan = rowspan;
      return defaultMerger;
    },
    // 合并表头
    mergeTableColumns() {
      const rowSelection = this.rowSelection;
      const rowSelectionType = Object.prototype.toString.call(rowSelection);
      if (rowSelectionType !== "[object Object]") return this.columns;
      const { type, fixed, width, title, align, disabled } = rowSelection;
      const hasType = ["checkbox", "radio"].includes(type);
      if (!hasType) return this.columns;
      const column = {
        fixed: fixed || false,
        width: width || 50,
        align: align || "center",
        title: title,
        type: type,
        disabled: disabled || false,
      };
      return [column].concat(this.columns);
    },
    getTableInstance() {
      return this.$refs["gm-table"];
    },
    // 获取唯一标识
    getRowKeyValue(record, index) {
      if (!this.rowKey) return index;
      if (typeof this.rowKey === "function") return this.rowKey(record);
      return record[this.rowKey];
    },
    // radio 发生事件
    radioChange(scope) {
      this.updateSelections([this.getRowKeyValue(scope.row, scope.$index)]);
      this.emitSelectionChange();
    },
    // checkbox 发生事件
    checkboxChange(checked, scope) {
      const selections = [...this.selections];
      const key = this.getRowKeyValue(scope.row, scope.$index);
      if (checked && !selections.includes(key)) {
        selections.push(key);
      } else if (!checked) {
        let index = -1;
        let length = selections.length;
        while (++index < length) {
          const value = selections[index];
          if (value === key) {
            selections.splice(index, 1);
            break;
          }
        }
      }
      this.updateSelections(selections);
      this.emitSelectionChange();
    },
    renderSelectionDisabled(scope, column) {
      const { disabled } = column;
      if (typeof disabled === "function")
        return disabled(scope.row, scope.$index);
      return disabled;
    },
    checkAllChange(check) {
      if (!check) {
        this.updateSelections([]);
      } else {
        const selections = [...this.selections];
        let index = -1;
        let length = this.dataSource.length;
        const disabled = this.rowSelection.disabled;
        if (disabled === true) return;
        while (++index < length) {
          const item = this.dataSource[index];
          const key = this.getRowKeyValue(item, index);
          if (typeof disabled === "function" && disabled(item, index)) {
            continue;
          }
          if (!selections.includes(key)) selections.push(key);
        }
        this.updateSelections(selections);
      }
      this.emitSelectionChange();
    },
    // currentPage 改变时会触发
    currentPageChange(currentPage) {
      this.emitPageChange(currentPage, this.pagination.pageSize);
    },
    // pageSize 改变时会触发
    pageSizeChange(pageSize) {
      this.emitPageChange(this.pagination.currentPage, pageSize);
    },
    // 发送 pageChange 事件
    emitPageChange(currentPage, pageSize) {
      this.$emit("pageChange", currentPage, pageSize);
    },
    // 选择器发生事件
    emitSelectionChange() {
      this.$nextTick(() => {
        const { change } = this.rowSelection;
        if (typeof change !== "function") return;
        const selectRows = [];
        let index = -1;
        let length = this.selections.length;
        while (++index < length) {
          const value = this.selections[index];
          const result = this.dataSource.find(
            (item, $index) => this.getRowKeyValue(item, $index) === value
          );
          if (result) {
            selectRows.push(result);
          }
        }
        change([...this.selections], selectRows);
      });
    },
    // 更新 selections
    updateSelections(selections) {
      this.$emit("update:selections", selections);
    },
  },
};
</script>

<style scoped lang="scss">
.common-table {
  ::v-deep {
    .el-table {
      .cell {
        // display: flex;
      }
      .el-button--text {
        color: #E53935;

        &.is-disabled {
          color: #c0c4cc;
        }
      }
    }
  }

  ::v-deep .el-table thead th {
    background-color: #fff;
  }

  .common-table-header {
    margin-bottom: 10px;
  }

  .ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    * {
      display: inline;
    }
  }

  .table-container {
    position: relative;
    background: #fff;
    padding: 10px;
    border-radius: 10px;
    //.table-loading {
    //  position: absolute;
    //  display: flex;
    //  align-items: center;
    //  justify-content: center;
    //  top: 0;
    //  left: 0;
    //  right: 0;
    //  bottom: 0;
    //  z-index: 9999;
    //  background-color: rgba(255, 255, 255, 0.7);
    //
    //  i {
    //    color: rgba(230, 162, 60, 0.5);
    //    font-size: 30px;
    //    font-weight: bolder;
    //  }
    //}
  }
}
</style>
