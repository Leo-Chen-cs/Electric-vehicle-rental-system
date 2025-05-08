<template>
  <div class="help-center">
    <t-navbar title="Help Center">
      <template #left>
        <div class="navbar-left-btn" @click="goToMy">
          <t-icon name="chevron-left" size="24px" />
        </div>
      </template>
    </t-navbar>

    <div class="help-content">
      <div class="search-box">
        <t-search
          v-model="searchValue"
          placeholder="Search help topics"
          :on-clear="onClear"
          :on-submit="onSubmit"
        />
      </div>

      <div class="faq-section">
        <t-collapse v-model="activeValues" expandIcon>
          <t-collapse-panel value="0" header="How to rent a scooter?">
            <div class="panel-content">
              <p>To rent a scooter, follow these simple steps:</p>
              <ol>
                <li>Find a nearby scooter on the map in the Home screen</li>
                <li>Tap on the scooter to view details</li>
                <li>Click "Rent" button and scan the QR code on the scooter</li>
                <li>Confirm payment method and start your ride</li>
              </ol>
            </div>
          </t-collapse-panel>
          
          <t-collapse-panel value="1" header="Payment methods">
            <div class="panel-content">
              <p>We accept the following payment methods:</p>
              <ul>
                <li>Credit/Debit cards</li>
                <li>PayPal</li>
                <li>In-app wallet</li>
                <li>Gift cards</li>
              </ul>
              <p>You can manage your payment methods in the Account settings.</p>
            </div>
          </t-collapse-panel>
          
          <t-collapse-panel value="2" header="Reporting issues with scooters">
            <div class="panel-content">
              <p>If you encounter any issues with a scooter, please report it through the following steps:</p>
              <ol>
                <li>Navigate to the scooter details page</li>
                <li>Click on "Report Issue" button</li>
                <li>Select the issue type and provide details</li>
                <li>Submit your report</li>
              </ol>
              <p>Our maintenance team will address the issue as soon as possible.</p>
            </div>
          </t-collapse-panel>
          
          <t-collapse-panel value="3" header="Billing and receipts">
            <div class="panel-content">
              <p>You can find your billing history and receipts in the Account section:</p>
              <ol>
                <li>Go to "My" tab</li>
                <li>Tap on "Ride History"</li>
                <li>Select any ride to view details and download receipt</li>
              </ol>
              <p>For any billing disputes, please contact our customer support.</p>
            </div>
          </t-collapse-panel>
          
          <t-collapse-panel value="4" header="Safety tips and regulations">
            <div class="panel-content">
              <p>Please follow these safety guidelines when using our scooters:</p>
              <ul>
                <li>Always wear a helmet</li>
                <li>Follow local traffic rules and regulations</li>
                <li>Do not ride on sidewalks where prohibited</li>
                <li>Park responsibly in designated areas</li>
                <li>Do not ride under the influence of alcohol or drugs</li>
                <li>Check the scooter before riding for any visible issues</li>
              </ul>
            </div>
          </t-collapse-panel>
        </t-collapse>
      </div>

      <div class="contact-section">
        <h3>Need more help?</h3>
        <t-button theme="primary" block @click="contactSupport">Contact Support</t-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { MessagePlugin } from 'tdesign-mobile-vue';

const router = useRouter();
const searchValue = ref('');
const activeValues = ref(['0']);

const goToMy = () => {
  router.push('/my');
};

const onClear = () => {
  searchValue.value = '';
};

const onSubmit = (value) => {
  MessagePlugin.info(`Searching for: ${value}`);
};

const contactSupport = () => {
  MessagePlugin.info('Opening support chat...');
  // Implementation for contacting support would go here
};
</script>

<style lang="scss" scoped>
.help-center {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.help-content {
  padding: 16px;
  padding-top: 76px; /* Reserve space for the navbar */
}

.navbar-left-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  margin-left: 8px;
  border-radius: 4px;
  cursor: pointer;
  
  .t-icon {
    color: #333;
    font-weight: bold;
  }
  
  &:active {
    background-color: rgba(0, 0, 0, 0.05);
  }
}

.search-box {
  margin-bottom: 16px;
}

.faq-section {
  margin-bottom: 24px;
  
  :deep(.t-collapse) {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  }
  
  :deep(.t-collapse-panel__header) {
    font-weight: 500;
  }
  
  .panel-content {
    padding: 8px 0;
    
    p {
      margin-bottom: 12px;
      line-height: 1.5;
    }
    
    ul, ol {
      padding-left: 20px;
      margin-bottom: 12px;
      
      li {
        margin-bottom: 8px;
        line-height: 1.5;
      }
    }
  }
}

.contact-section {
  background: #fff;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  
  h3 {
    font-size: 16px;
    margin-bottom: 16px;
    text-align: center;
    color: #333;
  }
}
</style>
