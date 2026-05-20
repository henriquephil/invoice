#!/usr/bin/env node
import { execSync } from "child_process";

const AWS_REGION   = "us-east-1";
const STATE_BUCKET = "hphil-invoice-tfstate";
const LOCK_TABLE   = "hphil-invoice-tflock";

function run(cmd) {
  try {
    execSync(cmd, { stdio: "pipe" });
    return true;
  } catch {
    return false;
  }
}

function runOrFail(cmd) {
  execSync(cmd, { stdio: "inherit" });
}

function checkDependency(name) {
  if (!run(`${name} --version`)) {
    console.error(`❌ '${name}' not found. Please install it before continuing.`);
    process.exit(1);
  }
}

console.log("🚀 Starting infrastructure bootstrap...\n");

checkDependency("aws");
checkDependency("terraform");

// ── S3 bucket for Terraform state ────────────────────────────────────────────

console.log("📦 Terraform state bucket...");

if (run(`aws s3api head-bucket --bucket ${STATE_BUCKET}`)) {
  console.log("   Already exists, skipping.\n");
} else {
  runOrFail(
    `aws s3api create-bucket --bucket ${STATE_BUCKET} --region ${AWS_REGION}`
  );

  runOrFail(
    `aws s3api put-bucket-versioning \
      --bucket ${STATE_BUCKET} \
      --versioning-configuration Status=Enabled`
  );

  runOrFail(
    `aws s3api put-public-access-block \
      --bucket ${STATE_BUCKET} \
      --public-access-block-configuration \
        "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"`
  );

  console.log("   ✅ Created.\n");
}

// ── DynamoDB table for state locking ─────────────────────────────────────────

console.log("🔒 DynamoDB state lock table...");

if (run(`aws dynamodb describe-table --table-name ${LOCK_TABLE} --region ${AWS_REGION}`)) {
  console.log("   Already exists, skipping.\n");
} else {
  runOrFail(
    `aws dynamodb create-table \
      --table-name ${LOCK_TABLE} \
      --attribute-definitions AttributeName=LockID,AttributeType=S \
      --key-schema AttributeName=LockID,KeyType=HASH \
      --billing-mode PAY_PER_REQUEST \
      --region ${AWS_REGION}`
  );

  console.log("   ✅ Created.\n");
}

// ── Done ──────────────────────────────────────────────────────────────────────

console.log("✅ Bootstrap complete! Next steps:\n");
console.log("   terraform init");
console.log("   terraform apply\n");