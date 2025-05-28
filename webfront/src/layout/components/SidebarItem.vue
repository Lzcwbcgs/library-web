<template>
  <div v-if="!item.meta || !item.meta.hidden">
    <template v-if="!hasChildren(item)">
      <el-menu-item :index="resolvePath(item.path)">
        <el-icon v-if="item.meta && item.meta.icon"><component :is="item.meta.icon" /></el-icon>
        <template #title>{{ item.meta && item.meta.title }}</template>
      </el-menu-item>
    </template>
    
    <el-sub-menu v-else :index="resolvePath(item.path)">
      <template #title>
        <el-icon v-if="item.meta && item.meta.icon"><component :is="item.meta.icon" /></el-icon>
        <span>{{ item.meta && item.meta.title }}</span>
      </template>
      
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(item.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import path from 'path-browserify'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

function hasChildren(item) {
  return item.children && item.children.length > 0 && !item.meta.hideChildrenInMenu
}

function resolvePath(routePath) {
  if (routePath.startsWith('/')) {
    return routePath
  }
  return path.resolve(props.basePath, routePath)
}
</script> 