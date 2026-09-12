<template>
  <el-table-column
    :label="column.title"
    :width="column.width"
    :align="column.align"
    :prop="column.key"
    :fixed="column.fixed"
  >
    <template v-if="!!(column.children && column.children.length > 0)">
      <generate-table-column
        v-for="($column, index) of column.children"
        :key="index" :column="$column"
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
                        item =>
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
    </template>
    <template #header="scope">
      <slot name="tableColumnHeader" :row="scope.row" :column="scope.column"
            :$column="column"></slot>
    </template>
    <slot slot-scope="scope" :row="scope.row" :$columnIndex="$columnIndex" :$index="scope.$index" :column="scope.column"
          :$column="column">
    </slot>
  </el-table-column>
</template>

<script>


// 渲染表头
const RenderTableHeader = {
  props: {
    renderHandler: {
      type: Function
    },
    scopeData: {
      type: Object
    }
  },
  render(createElement, context) {
    return this.renderHandler(createElement, this.scopeData)
  }
}

// template 里面没有办法直接使用需要中间层去处理
const RenderTableCellView = {
  props: {
    renderHandler: {
      type: Function
    },
    scopeData: {
      type: Object
    },
    propsKey: {
      type: String
    }
  },
  render(createElement) {
    if (typeof this.renderHandler !== 'function') return null
    return this.renderHandler(createElement, {
      ...this.scopeData,
      value: this.scopeData.row[this.propsKey]
    })
  }
}

export default {
  name: 'GenerateTableColumn',
  components: {
    RenderTableCellView,
    RenderTableHeader
  },
  props: {
    column: {
      type: Object,
      default: () => ({})
    },
    $columnIndex: {
      type: Number,
      default: 0
    }
  }
}
</script>

<style scoped>

</style>
