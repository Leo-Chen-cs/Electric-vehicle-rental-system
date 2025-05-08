<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Icon as TIcon } from 'tdesign-icons-vue-next';
import { useUserStore } from '@/store';
import { getUserInfo } from '@/service/user';

const route = useRoute();
const value = ref('Home');
const router = useRouter();
const userStore = useUserStore();
const list = ref([
  { value: 'Home', label: 'Home', icon: 'home' },
  { value: 'Records', label: 'Records', icon: 'history' },
  { value: 'My', label: 'My', icon: 'user' },
]);

// 检测页面路径来决定是否显示标签栏 - 修复路径匹配逻辑
const showTabBar = computed(() => {
  // 只有在特定页面才隐藏底部导航栏，确保主页和其他页面显示
  const currentPath = route.path;
  // 精确匹配路径，避免子路径误匹配
  return !currentPath.startsWith('/rent/') &&
    !currentPath.startsWith('/payment/') &&
    !currentPath.includes('login') &&
    !currentPath.includes('register')
    ;
});

// 检查token并获取用户信息
const checkTokenAndGetUserInfo = async () => {
  try {
    // 从localStorage获取token
    const token = localStorage.getItem('token');

    // 如果localStorage中有token但store中没有
    if (token && !userStore.userInfo.token) {
      userStore.setUserInfo({ token });
    }

    // 如果store中有token，尝试获取用户信息
    if (userStore.isLogined) {
      try {
        const response = await getUserInfo();
        if (response && response.code === 200) {
          // 更新用户信息，保留token
          userStore.setUserInfo({
            ...response.data,
            token: userStore.userInfo.token
          });
          console.log('用户信息获取成功');
        } else {
          // 获取用户信息失败，清除token
          console.log('获取用户信息失败，清除token');
          localStorage.removeItem('token');
          userStore.clearUserInfo();
        }
      } catch (error) {
        console.error('获取用户信息异常:', error);
        localStorage.removeItem('token');
        userStore.clearUserInfo();
      }
    }
  } catch (error) {
    console.error('Token处理异常:', error);
    localStorage.removeItem('token');
    userStore.clearUserInfo();
  }
};

const change = (changeValue) => {
  value.value = changeValue;
  router.push({
    name: changeValue,
  });
};

// 在组件挂载时检查token并获取用户信息
onMounted(() => {
  checkTokenAndGetUserInfo();
  
  // 添加事件监听，用于从其他组件更新底部导航栏状态
  document.addEventListener('updateTabBar', (event) => {
    if (event.detail) {
      value.value = event.detail;
    }
  });
});

// 在组件卸载时移除事件监听
onUnmounted(() => {
  document.removeEventListener('updateTabBar', () => {});
});
</script>

<template>
  <main class="app-container">
    <!-- 路由出口 -->
    <router-view />

    <t-tab-bar @change="change" v-model="value" theme="tag" :split="false" v-if="showTabBar" class="app-tab-bar">
      <t-tab-bar-item v-for="item in list" :key="item.value" :value="item.value">
        <template #icon>
          <t-icon :name="item.icon" />
        </template>
      </t-tab-bar-item>
    </t-tab-bar>
  </main>
</template>

<style>
.app-container {

  height: 100vh;
  background-color: #f5f7fa;
}

/* 确保底部导航栏始终显示在底部且不被覆盖 */
.app-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background-color: white;
}

:root {
  --td-brand-color: #0052d9;
}
</style>
