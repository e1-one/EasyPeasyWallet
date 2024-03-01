# EasyPeasyWallet

Demo project. Employ Pitest to enhance unit-tests coverage.

mvn clean install

Jacoco report is located here:

/target/site/jacoco/intex.html

mvn clean test-compile org.pitest:pitest-maven:mutationCoverage

Pitest report is located here:

/target/pit-reports/202402261711/com.easypeasy.wallet.exchange.services/WalletOperationsImpl.java.html