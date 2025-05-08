<template>
  <div class="about-us">
    <t-navbar title="About Us">
      <template #left>
        <div class="navbar-left-btn" @click="goToMy">
          <t-icon name="chevron-left" size="24px" />
        </div>
      </template>
    </t-navbar>

    <div class="about-content">
      <div class="logo-section">
        <t-image src="/scooter2.png" :style="{ width: '80px', height: '80px' }" />
        <h1>ScooterShare</h1>
        <div class="version">Version 1.0.0</div>
      </div>

      <div class="info-card">
        <h2>Our Mission</h2>
        <p>At ScooterShare, we're committed to revolutionizing urban mobility with eco-friendly, convenient, and affordable transportation solutions. Our electric scooters provide a sustainable alternative to traditional vehicles, reducing carbon emissions and traffic congestion.</p>
      </div>

      <div class="info-card">
        <h2>Our Story</h2>
        <p>Founded in 2022, ScooterShare began with a simple vision: to make cities more livable by providing accessible, clean transportation options. What started with just 50 scooters in one city has now grown to a fleet of thousands across multiple urban centers.</p>
        <p>Our team of dedicated professionals works tirelessly to ensure our scooters are reliable, safe, and always available when you need them.</p>
      </div>

      <div class="info-card">
        <h2>Contact Information</h2>
        <div class="contact-item">
          <t-icon name="mail" />
          <span>support@scootershare.com</span>
        </div>
        <div class="contact-item">
          <t-icon name="call" />
          <span>+1 (800) 123-4567</span>
        </div>
        <div class="contact-item">
          <t-icon name="location" />
          <span>123 Urban Mobility St, Innovation City, CA 94103</span>
        </div>
      </div>

      <div class="info-card">
        <h2>Social Media</h2>
        <div class="social-links">
          <div class="social-btn">
            <t-icon name="logo-twitter" />
            <span>Twitter</span>
          </div>
          <div class="social-btn">
            <t-icon name="logo-instagram" />
            <span>Instagram</span>
          </div>
          <div class="social-btn">
            <t-icon name="logo-facebook" />
            <span>Facebook</span>
          </div>
        </div>
      </div>

      <div class="legal-links">
        <div class="link" @click="showTerms">Terms of Service</div>
        <div class="link" @click="showPrivacy">Privacy Policy</div>
        <div class="link" @click="showLicenses">Licenses</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { MessagePlugin } from 'tdesign-mobile-vue';

const router = useRouter();

const goToMy = () => {
  router.push('/my');
};

const showTerms = () => {
  MessagePlugin.info('Terms of Service will open here');
};

const showPrivacy = () => {
  MessagePlugin.info('Privacy Policy will open here');
};

const showLicenses = () => {
  MessagePlugin.info('Licenses information will open here');
};
</script>

<style lang="scss" scoped>
.about-us {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.about-content {
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

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
  
  h1 {
    margin-top: 16px;
    margin-bottom: 4px;
    font-size: 24px;
    font-weight: bold;
    color: #0052d9;
  }
  
  .version {
    font-size: 14px;
    color: #888;
  }
}

.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  
  h2 {
    font-size: 18px;
    margin-bottom: 12px;
    color: #333;
    font-weight: 600;
  }
  
  p {
    font-size: 14px;
    line-height: 1.6;
    color: #666;
    margin-bottom: 12px;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  
  .t-icon {
    color: #0052d9;
    margin-right: 12px;
    font-size: 18px;
  }
  
  span {
    font-size: 14px;
    color: #666;
  }
}

.social-links {
  display: flex;
  justify-content: space-around;
  
  .social-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px;
    
    .t-icon {
      font-size: 24px;
      color: #0052d9;
      margin-bottom: 8px;
    }
    
    span {
      font-size: 12px;
      color: #666;
    }
  }
}

.legal-links {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 16px;
  margin-bottom: 32px;
  
  .link {
    color: #0052d9;
    font-size: 14px;
    padding: 8px 12px;
    cursor: pointer;
    
    &:after {
      content: '•';
      margin-left: 12px;
      color: #ccc;
    }
    
    &:last-child:after {
      content: '';
      margin-left: 0;
    }
  }
}
</style>
