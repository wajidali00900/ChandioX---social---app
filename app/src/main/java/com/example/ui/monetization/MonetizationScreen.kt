package com.example.ui.monetization

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.theme.FbBlue
import com.example.ui.theme.FbGreenOnline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetizationScreen(
    user: User,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var showPolicyDialog by remember { mutableStateOf(false) }
    var showPayoutSetupDialog by remember { mutableStateOf(false) }
    var isPayoutConfigured by remember { mutableStateOf(false) }
    var hasAppliedForMonetization by remember { mutableStateOf(false) }

    // User's current metrics vs Eligibility Requirements
    val followersTarget = 1000
    val likesTarget = 10000
    val videoViewsTarget = 10000
    val videoPlaysTarget = 10000

    val currentFollowers = user.followersCount // e.g. 4210
    var currentLikes by remember { mutableIntStateOf(10450) }
    var currentVideoViews by remember { mutableIntStateOf(12800) }
    var currentVideoPlays by remember { mutableIntStateOf(11240) }

    val isFollowersMet = currentFollowers >= followersTarget
    val isLikesMet = currentLikes >= likesTarget
    val isViewsMet = currentVideoViews >= videoViewsTarget
    val isPlaysMet = currentVideoPlays >= videoPlaysTarget

    val metCount = listOf(isFollowersMet, isLikesMet, isViewsMet, isPlaysMet).count { it }
    val isFullyEligible = metCount == 4

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Monetization & Earnings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Profile"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showPolicyDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Monetization Policy",
                            tint = FbBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Earnings Overview Hero Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Estimated Revenue",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (hasAppliedForMonetization || isPayoutConfigured) "$248.80" else "$0.00",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FbGreenOnline
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFullyEligible) FbGreenOnline.copy(alpha = 0.15f) else Color(0xFFF7B125).copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFullyEligible) Icons.Default.CheckCircle else Icons.Default.Pending,
                                    contentDescription = null,
                                    tint = if (isFullyEligible) FbGreenOnline else Color(0xFFF7B125),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isFullyEligible) "Eligible to Earn" else "$metCount/4 Criteria Met",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFullyEligible) FbGreenOnline else Color(0xFFF7B125)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        EarningMetric(label = "In-Stream Ads", value = if (hasAppliedForMonetization) "$142.50" else "$0.00")
                        EarningMetric(label = "Reels Bonuses", value = if (hasAppliedForMonetization) "$84.20" else "$0.00")
                        EarningMetric(label = "Stars & Gifts", value = if (hasAppliedForMonetization) "$22.10" else "$0.00")
                    }
                }
            }

            // Criteria Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monetization Eligibility",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(onClick = { showPolicyDialog = true }) {
                            Text("Policy Rules", color = FbBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Text(
                        text = "Meet all 4 required milestones to activate monetization and start receiving payouts on ChandioX Social:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    // 1. 1k Followers
                    EligibilityCriterionItem(
                        icon = Icons.Default.People,
                        title = "1,000 Followers",
                        description = "Reach at least 1k active followers on your profile.",
                        current = currentFollowers,
                        target = followersTarget,
                        displayCurrent = "${currentFollowers}",
                        displayTarget = "1,000",
                        isMet = isFollowersMet
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. 10k Likes
                    EligibilityCriterionItem(
                        icon = Icons.Default.ThumbUp,
                        title = "10,000 Likes",
                        description = "Accumulate 10k total likes across your feed posts and reels.",
                        current = currentLikes,
                        target = likesTarget,
                        displayCurrent = "${currentLikes}",
                        displayTarget = "10,000",
                        isMet = isLikesMet
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. 10k Video Views
                    EligibilityCriterionItem(
                        icon = Icons.Default.OndemandVideo,
                        title = "10,000 Video Views",
                        description = "Generate 10k views on your published videos in the Watch tab.",
                        current = currentVideoViews,
                        target = videoViewsTarget,
                        displayCurrent = "${currentVideoViews}",
                        displayTarget = "10,000",
                        isMet = isViewsMet
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4. 10k Video Plays
                    EligibilityCriterionItem(
                        icon = Icons.Default.PlayCircle,
                        title = "10,000 Video Plays",
                        description = "Reach 10k reel and video playbacks across all media.",
                        current = currentVideoPlays,
                        target = videoPlaysTarget,
                        displayCurrent = "${currentVideoPlays}",
                        displayTarget = "10,000",
                        isMet = isPlaysMet
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Apply / Set Up Payout Button
                    Button(
                        onClick = {
                            if (isFullyEligible) {
                                showPayoutSetupDialog = true
                            } else {
                                Toast.makeText(context, "Complete all 4 criteria to apply for monetization.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("apply_monetization_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFullyEligible) FbBlue else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isFullyEligible) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (hasAppliedForMonetization) Icons.Default.Verified else Icons.Default.MonetizationOn,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                hasAppliedForMonetization -> "Monetization Active (Payouts Enabled)"
                                isFullyEligible -> "Apply for Monetization & Payouts"
                                else -> "Unlock Monetization ($metCount/4 Met)"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // Monetization Policy Details Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = FbBlue)
                        Text(
                            text = "ChandioX Monetization Policies",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    PolicyRuleRow(
                        title = "Authentic & Original Content",
                        desc = "Only original videos, photos, and writing created by you are eligible for ad revenue shares."
                    )
                    PolicyRuleRow(
                        title = "Community Standards Compliance",
                        desc = "Accounts must maintain a clean record with zero community violations, copyright strikes, or spam reports."
                    )
                    PolicyRuleRow(
                        title = "Advertiser-Friendly Guidelines",
                        desc = "Content must be safe for mainstream brand sponsors. Profanity, violence, or sensitive topics may be demonetized."
                    )
                    PolicyRuleRow(
                        title = "Monthly Payout Schedule",
                        desc = "Earnings are deposited on the 21st of every month via Bank Transfer or Digital Payout (minimum $50 balance threshold)."
                    )
                }
            }
        }
    }

    // Payout Setup Dialog
    if (showPayoutSetupDialog) {
        PayoutSetupDialog(
            user = user,
            onDismiss = { showPayoutSetupDialog = false },
            onConfigured = { method, account ->
                isPayoutConfigured = true
                hasAppliedForMonetization = true
                showPayoutSetupDialog = false
                Toast.makeText(context, "Monetization approved! Payout method ($method) linked successfully.", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Policy Dialog
    if (showPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPolicyDialog = false },
            icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = FbBlue) },
            title = { Text("Monetization Terms & Criteria", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "To protect advertisers and maintain quality across ChandioX Social, creators must meet these strict criteria:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("1. Minimum 1,000 Followers", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("2. Minimum 10,000 Likes on published content", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("3. Minimum 10,000 Video Views in Watch tab", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("4. Minimum 10,000 Video Plays across Reels & clips", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Text(
                        text = "Revenue Sharing Breakdown:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text("• In-Stream Ads: 55% Creator / 45% Platform", fontSize = 13.sp)
                    Text("• Reels Play Bonuses: 100% of bonus pool goals", fontSize = 13.sp)
                    Text("• Stars & Gifts: 70% direct payout", fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPolicyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FbBlue)
                ) {
                    Text("I Understand")
                }
            }
        )
    }
}

@Composable
private fun EarningMetric(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun EligibilityCriterionItem(
    icon: ImageVector,
    title: String,
    description: String,
    current: Int,
    target: Int,
    displayCurrent: String,
    displayTarget: String,
    isMet: Boolean
) {
    val progress = (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (isMet) FbGreenOnline.copy(alpha = 0.2f) else FbBlue.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isMet) FbGreenOnline else FbBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isMet) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = FbGreenOnline,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Eligible",
                        color = FbGreenOnline,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "$displayCurrent / $displayTarget",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (isMet) FbGreenOnline else FbBlue,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun PolicyRuleRow(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = FbBlue,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PayoutSetupDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfigured: (method: String, account: String) -> Unit
) {
    var payoutMethod by remember { mutableStateOf("Bank Account") }
    var legalName by remember { mutableStateOf(user.name) }
    var accountNumber by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("National Bank / IBAN") }
    val methods = listOf("Bank Account", "PayPal", "EasyPaisa / JazzCash", "Stripe")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = FbBlue)
                Text("Set Up Payout Account", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select your payout method to receive monthly ad revenue and bonuses:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Methods Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.take(2).forEach { m ->
                        FilterChip(
                            selected = payoutMethod == m,
                            onClick = { payoutMethod = m },
                            label = { Text(m, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    methods.drop(2).forEach { m ->
                        FilterChip(
                            selected = payoutMethod == m,
                            onClick = { payoutMethod = m },
                            label = { Text(m, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = legalName,
                    onValueChange = { legalName = it },
                    label = { Text("Legal Beneficiary Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text(if (payoutMethod == "PayPal") "PayPal Email Address" else "Account Number / IBAN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "🔒 Payout details are encrypted and processed automatically on the 21st of each month.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (legalName.isNotBlank() && accountNumber.isNotBlank()) {
                        onConfigured(payoutMethod, accountNumber)
                    }
                },
                enabled = legalName.isNotBlank() && accountNumber.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = FbBlue)
            ) {
                Text("Confirm Payout Setup")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
