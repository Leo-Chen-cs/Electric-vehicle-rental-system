<template>
    <transition name="fade">
      <div v-if="visible" class="agreement-modal-overlay" @click="handleClose">
        <div class="agreement-modal-container" @click.stop>
          <div class="modal-header">
            <h3>Rental Agreement</h3>
            <div class="close-icon" @click="handleClose">
              <span>×</span>
            </div>
          </div>
          
          <div class="modal-body" ref="modalBodyRef" @scroll="handleScroll">
            <div class="agreement-content">
              <h4>Terms and Conditions</h4>
              <p>By agreeing to these terms, you acknowledge that you will:</p>
              
              <h5>Riding Safety</h5>
              <ul>
                <li>Wear a helmet at all times while riding</li>
                <li>Follow all traffic rules and regulations</li>
                <li>Not ride under the influence of alcohol or drugs</li>
                <li>Only ride in designated areas and bike lanes when available</li>
                <li>Not carry any passengers on the scooter</li>
              </ul>
              
              <h5>Charging and Parking</h5>
              <ul>
                <li>Park the scooter in designated parking areas only</li>
                <li>Not block pedestrian walkways or building entrances</li>
                <li>Return the scooter with at least 20% battery remaining</li>
              </ul>
              
              <h5>Liability</h5>
              <ul>
                <li>You are responsible for any damages caused to the scooter during your rental period</li>
                <li>You accept full responsibility for any accidents or injuries that occur during your rental</li>
                <li>You will not hold the company liable for injuries sustained while using the service</li>
              </ul>
              
              <h5>Ethics and Community</h5>
              <ul>
                <li>Respect the local community and pedestrians</li>
                <li>Ride at appropriate speeds in crowded areas</li>
                <li>Report any scooter damages or issues through the app</li>
                <li>Do not tamper with the scooter's hardware or software</li>
              </ul>
              
              <p class="final-note">By clicking "Agree & Continue", you confirm that you have read, understood, and agree to abide by these terms and conditions.</p>
            </div>
          </div>
          
          <div class="modal-footer">
            <button class="btn-cancel" @click="handleClose">Cancel</button>
            <button class="btn-agree" :disabled="!hasReadToBottom" @click="handleAgree">
              Agree & Continue
            </button>
          </div>
        </div>
      </div>
    </transition>
  </template>
  
  <script setup>
  import { defineProps, defineEmits, ref, onMounted, watchEffect } from 'vue';
  
  const props = defineProps({
    visible: {
      type: Boolean,
      default: false
    }
  });
  
  const emits = defineEmits(['close', 'agree']);
  const modalBodyRef = ref(null);
  const hasReadToBottom = ref(false);
  
  const handleClose = () => {
    emits('close');
  };
  
  const handleAgree = () => {
    emits('agree');
  };
  
  // 检查用户是否已滚动到底部
  const handleScroll = () => {
    if (!modalBodyRef.value) return;
    
    const { scrollTop, scrollHeight, clientHeight } = modalBodyRef.value;
    // 当滚动到内容底部（留5px的误差空间）时，标记为已阅读完成
    if (scrollHeight - scrollTop - clientHeight < 5) {
      hasReadToBottom.value = true;
    }
  };
  
  // 当模态框显示状态改变时重置阅读状态
  const resetReadState = () => {
    hasReadToBottom.value = false;
    // 确保滚动位置重置到顶部
    if (modalBodyRef.value) {
      modalBodyRef.value.scrollTop = 0;
    }
  };
  
  // 监听visible属性变化，当重新打开模态框时重置阅读状态
  onMounted(() => {
    watchEffect(() => {
      if (props.visible) {
        resetReadState();
      }
    });
  });
  </script>
  
  <style lang="scss" scoped>
  .agreement-modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1100;
  }
  
  .agreement-modal-container {
    width: 90%;
    max-width: 500px;
    max-height: 80vh;
    background-color: #fff;
    border-radius: 12px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
  
  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #eee;
    
    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 500;
      color: #333;
    }
    
    .close-icon {
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-size: 22px;
      color: #000;
    }
  }
  
  .modal-body {
    flex: 1;
    overflow-y: auto;
    padding: 16px;
    
    .agreement-content {
      h4 {
        font-size: 18px;
        margin-top: 0;
        margin-bottom: 16px;
        color: #333;
      }
      
      h5 {
        font-size: 16px;
        margin-top: 20px;
        margin-bottom: 8px;
        color: #0066ff;
      }
      
      p {
        margin-bottom: 16px;
        font-size: 14px;
        line-height: 1.5;
        color: #666;
      }
      
      ul {
        margin-bottom: 16px;
        padding-left: 20px;
        
        li {
          margin-bottom: 8px;
          font-size: 14px;
          line-height: 1.5;
          color: #666;
        }
      }
      
      .final-note {
        font-weight: 500;
        margin-top: 20px;
        padding: 8px;
        background-color: #f9f9f9;
        border-radius: 4px;
      }
    }
  }
  
  .modal-footer {
    display: flex;
    padding: 16px;
    border-top: 1px solid #eee;
    
    button {
      flex: 1;
      height: 44px;
      border-radius: 22px;
      font-size: 16px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s;
      
      &.btn-cancel {
        margin-right: 12px;
        background-color: #f5f5f5;
        border: none;
        color: #666;
        font-weight: bold;
        
        &:hover {
          background-color: #e0e0e0;
        }
      }
      
      &.btn-agree {
        background-color: #0066ff;
        border: none;
        color: white;
        font-weight: bold;
        
        &:hover:not(:disabled) {
          background-color: #0052cc;
        }
        
        &:disabled {
          background-color: #99c2ff;
          cursor: not-allowed;
          opacity: 0.7;
        }
      }
    }
  }
  
  // 淡入淡出动画
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.3s;
  }
  
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
  </style>