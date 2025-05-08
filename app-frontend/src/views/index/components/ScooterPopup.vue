<template>
  <transition name="slide-up">
    <div v-if="visible" class="scooter-popup-container" @click.stop>
      <div class="scooter-popup-content">
        <div class="popup-header">
          <h3>Scooter Details</h3>
          <div class="close-icon" @click="handleClose">
            <span>×</span>
          </div>
        </div>
        
        <div class="popup-body">
          <!-- ID -->
          <div class="info-section">
            <div class="info-label">Scooter ID:</div>
            <div class="info-value-id">#{{ scooter.id }}</div>
          </div>
          
          <!-- Price -->
          <div class="price-section">
            <div class="price-info">
              <span class="price-amount">1</span>
              <span class="price-currency">£</span>
              <span class="price-text">starting price</span>
            </div>
            
            <!-- Scooter brand image -->
            <div class="scooter-image">
              <img :src="getScooterImage(scooter.brand)" alt="Scooter" />
            </div>
          </div>
          
          <!-- Battery -->
          <div class="info-section">
            <div class="info-label">Battery Level:</div>
            <div class="info-value-battery">{{ scooter.batteryLevel }}%</div>
          </div>
          <div class="battery-indicator">
            <div class="battery-level" :style="{width: `${scooter.batteryLevel}%`, backgroundColor: getBatteryColor(scooter.batteryLevel)}"></div>
          </div>
          
          <!-- Status -->
          <div class="info-section">
            <div class="info-label">Status:</div>
            <div class="info-value-status">{{ scooter.status }}</div>
          </div>
        </div>
        
        <div class="popup-footer">
          <button class="btn-cancel" @click="handleClose">Cancel</button>
          <button class="btn-rent" @click="showAgreement">Rent</button>
        </div>
      </div>
    </div>
  </transition>

  <rental-agreement-modal 
    :visible="showAgreementModal" 
    @close="closeAgreementModal" 
    @agree="confirmRent"
  />
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue';
import RentalAgreementModal from './RentalAgreementModal.vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  scooter: {
    type: Object,
    default: () => ({
      id: '',
      batteryLevel: 0,
      status: '',
      brand: '' // Add default value for brand
    })
  }
});

const emits = defineEmits(['close', 'rent']);
const showAgreementModal = ref(false);

const handleClose = () => {
  emits('close');
};

// 显示协议模态框，而不是直接触发租赁
const showAgreement = () => {
  showAgreementModal.value = true;
};

// 关闭协议模态框
const closeAgreementModal = () => {
  showAgreementModal.value = false;
};

// 用户同意协议后才触发租赁
const confirmRent = () => {
  closeAgreementModal.value = false;
  emits('rent', props.scooter);
};

const getBatteryColor = (level) => {
  if (level > 70) return '#4CAF50';
  if (level > 30) return '#FFC107';
  return '#F44336';
};

// Get scooter image based on brand
const getScooterImage = (brand) => {
  if (!brand) return ''; // Return empty string if no brand information
  try {
    // Try to load the corresponding image
    return `/${brand}.png`;
  } catch (error) {
    console.error('Failed to load scooter image:', error);
    return ''; // Return empty string if loading fails
  }
};
</script>

<style lang="scss" scoped>
.scooter-popup-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  max-height: 80vh;
  background-color: #fff;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.scooter-popup-content {
  padding: 0;
}

.popup-header {
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

.popup-body {
  padding: 20px 16px;

  .info-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    
    .info-label {
      font-size: 15px;
      color: #666;
    }
    
    .info-value-id {
      font-size: 15px;
      font-weight: 500;
      color: #333;
    }
    
    .info-value-battery {
      font-size: 15px;
      font-weight: bold;
      color: #000;
    }
    
    .info-value-status {
      font-size: 15px;
      font-weight: bold;
      color: #0066ff;
    }
  }
  
  .price-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 20px 0;
    
    .price-info {
      display: flex;
      align-items: baseline;
      
      .price-amount {
        font-size: 36px;
        font-weight: 700;
        color: #333;
        line-height: 1;
      }
      
      .price-currency {
        font-size: 22px;
        font-weight: 500;
        color: #333;
        margin-right: 4px;
      }
      
      .price-text {
        font-size: 14px;
        color: #000;
        margin-left: 4px;
      }
    }
    
    // Scooter image styles
    .scooter-image {
      width: 150px;
      height: 100px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      img {
        max-width: 100%;
        max-height: 100%;
        object-fit: contain;
      }
    }
  }
  
  .battery-indicator {
    height: 6px;
    background-color: #eee;
    border-radius: 3px;
    margin-bottom: 12px;
    overflow: hidden;
    
    .battery-level {
      height: 100%;
      transition: width 0.3s ease;
    }
  }
}

.popup-footer {
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
    
    &.btn-rent {
      background-color: #0066ff;
      border: none;
      color: white;
      font-weight: bold;

      &:hover {
        background-color: #0052cc;
      }
    }
  }
}

// Slide up animation
.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.3s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
}

.slide-up-enter-to,
.slide-up-leave-from {
  transform: translateY(0);
}
</style>